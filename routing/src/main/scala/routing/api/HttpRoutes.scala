package routing.api

import map_repository.cache.MapInfo
import map_repository.db.Points
import map_repository.model.Point
import org.http4s.headers
import routing.graph.AStar
import zio.ZIO
import zio.http._
import zio.http.model.{Method, Status}
import zio.json._
import zio.stream.{ZPipeline, ZSink}

import java.nio.file.Files

case class AuthorizationToken(token: String)
object AuthorizationToken {
  implicit val encoder: JsonEncoder[AuthorizationToken] =
    DeriveJsonEncoder.gen[AuthorizationToken]
}

case class IdMapPoint(value: String)
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
  val MaxSize: Long = 10L * 1024L * 1024L // 10 MB
  val DestinationDir: String = "imgs"

  val app: HttpApp[Points with MapInfo.Service, Response] =
    Http.collectZIO[Request] {
      case req @ Method.POST -> !! / "route" / "search" => {
        (for {
          list_data <- req.body.asString
            .map(body => body.fromJson[List[IdMapPoint]])
            .right
          result <- route(list_data)
        } yield (result)).orElseFail(Response.status(Status.BadRequest))
      }
      case req @ Method.POST -> !! / "upload" / fileName
          if req.contentType.contains(
            headers.`Content-Type`(org.http4s.MediaType.image.jpeg)
          ) && req.contentLength.getOrElse(0L) <= MaxSize =>
        val jpegValidationResult = req.body.asStream.through(JpegValidation.pipeline).runDrain

        (for {
           _ <- jpegValidationResult
          path <- ZIO.attempt(
            Files.createFile(java.nio.file.Paths.get(DestinationDir, fileName))
          )
          _ <- req.body.asStream
            .via(ZPipeline.deflate())
            .run(ZSink.fromPath(path))
        } yield (Response.status(Status.Ok)))
          .orElseFail(Response.status(Status.BadRequest))
    }

  private def getPointById(id: Int, points: Array[Point]): Point = {
    points.find(p => p.id == id).orNull
  }

  private def route(
      routing_points: List[IdMapPoint]
  ): ZIO[Points with MapInfo.Service, Throwable, Response] = {
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
    } yield ({
      Response.json(
        AStar
          .aStar(
            getPointById(routing_points.head.value.toInt, points),
            getPointById(routing_points(1).value.toInt, points),
            edges
          )
          .toJson
      )
    }))
  }
}
