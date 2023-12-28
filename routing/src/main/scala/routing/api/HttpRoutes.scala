package routing.api

import map_repository.cache.MapInfo
import map_repository.db.Points
import map_repository.db.Edges
import map_repository.model.Point
import routing.graph.AStar
import zio.ZIO
import zio.http._
import zio.http.model.{Method, Status}
import zio.json._
import nl.vroste.rezilience.CircuitBreaker.{CircuitBreakerOpen, WrappedError}
import circuitbreaker.{JamValue, JamsIntegration}
import circuitbreaker.ZioCircuitBreaker

import scala.collection.concurrent.TrieMap

case class IdMapPoint(value: Int)
object IdMapPoint {
  implicit val encoder: JsonEncoder[IdMapPoint] =
    DeriveJsonEncoder.gen[IdMapPoint]
  implicit val decoder: JsonDecoder[IdMapPoint] =
    DeriveJsonDecoder.gen[IdMapPoint]
}

case class MapPoint(id: IdMapPoint, name: String)
object MapPoint {
  implicit val encoder: JsonEncoder[MapPoint] = DeriveJsonEncoder.gen[MapPoint]
  implicit val decoder: JsonDecoder[MapPoint] = DeriveJsonDecoder.gen[MapPoint]
}

object HttpRoutes {
  val fallbackJam: TrieMap[Int, JamValue] = TrieMap.empty

  val app: HttpApp[
    MapInfo.Service with Points with Edges with JamsIntegration with ZioCircuitBreaker,
    Response
  ] =
    Http.collectZIO[Request] {
      case req @ Method.POST -> !! / "route" / "search" => {
        (for {
          list_data <- req.body.asString
            .map(body => body.fromJson[List[IdMapPoint]])
            .right
          result <- route(list_data)
        } yield result).orElseFail(Response.status(Status.BadRequest))
      }
    }

  private def getPointById(id: Int, points: Array[Point]): Point = {
    points.find(p => p.id == id).orNull
  }

  private def route(
      routing_points: List[IdMapPoint]
  ): ZIO[
    MapInfo.Service with Points with Edges with JamsIntegration with ZioCircuitBreaker,
    Throwable,
    Response
  ] = {
    (for {
      points <- MapInfo.Service.getPoints()
      edges <- MapInfo.Service
        .getEdges()
        .map(_.groupBy(_.pointFrom).map { case (k, v) =>
          getPointById(k, points) -> v
            .map(_.pointTo)
            .map(getPointById(_, points))
            .toList
        })
      from <- ZIO.succeed(routing_points.head.value)
      to <- ZIO.succeed(routing_points(1).value)
      path <- ZIO.succeed(
        AStar
          .aStar(
            getPointById(from, points),
            getPointById(to, points),
            edges
          )
      )
      jam <-
        ZioCircuitBreaker
          .run(JamsIntegration.getJam(from))
          .tap(jam => ZIO.succeed(fallbackJam.put(from, jam)))
          .catchAll(error =>
            fallbackJam.get(from) match {
              case Some(data) =>
                ZIO.logInfo(s"Get data from fallback $data") *> ZIO.succeed(
                  data
                )
              case None =>
                ZIO.logError(s"Get error from jams ${error.toString}") *>
                  ZIO.fail(error)
            }
          )
    } yield (path, jam)).either.map {
      case Right((path, jam)) if path == List.empty =>
        Response.status(Status.NoContent)
      case Right((path, jam)) =>
        Response.json(
          Map[String, String]("path" -> path.toJson, "jam" -> jam.toJson).toJson
        )
      case Left((path, jam)) => Response.status(Status.NoContent)
    }
  }
}
