package routing.api

import map_repository.cache.MapInfo
import map_repository.db.Points
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
        // (for {
        //   x <- Points.foo()
        //   // _ <- Points.getAll()
        // } yield (Response.json(
        //   List(
        //     MapPoint(IdMapPoint("31234"), "Кремль"),
        //     MapPoint(IdMapPoint("0"), "МФТИ")
        //   ).toJson
        // ))).orElseFail(Response.status(Status.BadRequest))
        // {
        //   req.body.asString.map(body =>
        //     body.fromJson[List[IdMapPoint]] match {
        //       // case Left(_)     => Response.status(Status.Forbidden)
        //       case Right(data) => route(data)
        //     }
        //   )
        // }.orElseFail(Response.status(Status.BadRequest))
      }
    }

  private def route(
      credentials: List[IdMapPoint]
  ): ZIO[Points with MapInfo.Service, Throwable, Response] = {
    (for {
      // points <- Points.getAll().runCollect.map(_.toArray)
      points_2 <- MapInfo.Service.getPoints()
      // _ <- Points.getAll()
    } yield ({
      println(points_2)
      // println(points(0))

      Response.json(
        List(
          MapPoint(IdMapPoint("31234"), "Кремль"),
          MapPoint(IdMapPoint("0"), "МФТИ")
        ).toJson
      )
    }))

    // Points.getAll().runCollect.map(_.toArray).either.flatMap({
    //   case Right(arr) =>
    //     println(arr)
    //     ZIO.fail(new Exception("Biba"))
    //   case Left(e) =>
    //     ZIO.fail(e)
    // })
    // take(1).map(p => println(p.toString))

  }
}
