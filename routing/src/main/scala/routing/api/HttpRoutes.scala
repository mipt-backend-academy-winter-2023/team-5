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
  val app: HttpApp[MapInfo.Service with Points with Edges, Response] =
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
  ): ZIO[MapInfo.Service with Points with Edges, Throwable, Response] = {
    for {
      points <- MapInfo.Service.getPoints()
      edges <- MapInfo.Service
        .getEdges()
        .map(_.groupBy(_.pointFrom).map { case (k, v) =>
          getPointById(k, points) -> v
            .map(_.pointTo)
            .map(getPointById(_, points))
            .toList
        })
      path = AStar
        .aStar(
          getPointById(routing_points.head.value.toInt, points),
          getPointById(routing_points(1).value.toInt, points),
          edges
        )
    } yield {
      if (path != List.empty[Point]) {
        Response.json(
          path.toJson
        )
      } else {
        Response.status(Status.NoContent)
      }
    }
  }
}
