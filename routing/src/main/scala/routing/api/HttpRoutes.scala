package routing.api

import zio.{Task, ZIO}
import zio.http._
import zio.http.model.{Method, Status}
import zio.json._

case class AuthorizationToken(token: String)
object AuthorizationToken {
  implicit val encoder: JsonEncoder[AuthorizationToken] = DeriveJsonEncoder.gen[AuthorizationToken]
}

case class IdMapPoint(value: String)
object IdMapPoint {
  implicit val encoder: JsonEncoder[IdMapPoint] = DeriveJsonEncoder.gen[IdMapPoint]
  implicit val decoder: JsonDecoder[IdMapPoint] = DeriveJsonDecoder.gen[IdMapPoint]
}

case class MapPoint(id: IdMapPoint, name: String)
object MapPoint {
  implicit val encoder: JsonEncoder[MapPoint] = DeriveJsonEncoder.gen[MapPoint]
  implicit val decoder: JsonDecoder[MapPoint] = DeriveJsonDecoder.gen[MapPoint]
}

object HttpRoutes {
  val app: HttpApp[Any, Response] =
    Http.collectZIO[Request] {
      // Ключиков нет в ямле, делать не буду ибо волк заходит в бар
      case req @ Method.POST -> !! / "route" / "search" => {
        req.body.asString.map(body =>
          body.fromJson[List[IdMapPoint]] match {
            case Left(_) => Response.status(Status.Forbidden)
            case Right(data) => route(data)
          })
      }.orElseFail(Response.status(Status.BadRequest))
    }

  private def route(credentials: List[IdMapPoint]): Response = {
    Response.json(List(MapPoint(IdMapPoint("Тутдым"), "сюдым"), MapPoint(IdMapPoint("чота там"), "кому то там")).toJson)
  }
}
