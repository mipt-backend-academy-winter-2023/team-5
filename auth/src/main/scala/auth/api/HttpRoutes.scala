package auth.api

import zio.{ZIO, Task}
import zio.http._
import zio.http.model.{Method, Status}
import zio.json._

case class UserCredentials(login: String, password: String)
object UserCredentials {
  implicit val decoder: JsonDecoder[UserCredentials] = DeriveJsonDecoder.gen[UserCredentials]
}

case class AuthorizationToken(token: String)
object AuthorizationToken {
  implicit val encoder: JsonEncoder[AuthorizationToken] = DeriveJsonEncoder.gen[AuthorizationToken]
}

object HttpRoutes {
  val app: HttpApp[Any, Response] =
    Http.collectZIO[Request] {
      case req @ Method.POST -> !! / "auth" / "login" => {
        req.body.asString.map(body =>
          body.fromJson[UserCredentials] match {
          case Left(_) => Response.status(Status.Forbidden)
          case Right(data) => authenticate(data)
        })
      }.orElseFail(Response.status(Status.BadRequest))
      case req@Method.POST -> !! / "auth" / "register" => {
        req.body.asString.map(body =>
          body.fromJson[UserCredentials] match {
            case Left(_) => Response.status(Status.Forbidden)
            case Right(data) => Response.status(Status.Ok)
          })
      }.orElseFail(Response.status(Status.BadRequest))
    }

  private def authenticate(credentials: UserCredentials): Response = {
    Response.json(AuthorizationToken("dummy-token").toJson)
  }
}
