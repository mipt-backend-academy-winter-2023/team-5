package routing.api

import map_repository.cache.MapInfo
import map_repository.db.Points
import routing.graph.AStar
import zio.ZIO
import zio.http._
import zio.http.model.{Method, Status}
import zio.json._
// import org.postgis.Point

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
    }

  private def route(
      credentials: List[IdMapPoint]
  ): ZIO[Points with MapInfo.Service, Throwable, Response] = {
    (for {
      points <- MapInfo.Service.getPoints()
      edges <- MapInfo.Service
        .getEdges()
        .map(_.groupBy(_.pointFrom).map { case (k, v) =>
          k -> v.map(_.pointTo)
        })
    } yield ({
      println(points(0))
      println(edges)

      Response.json(
        AStar
          .aStar(
            points(credentials.head.value.toInt),
            points(credentials(1).value.toInt),
            points,
            edges
          )
          .toJson
      )
    }))
  }
}
