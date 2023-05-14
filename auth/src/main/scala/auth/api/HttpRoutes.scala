package auth.api

import repository.db.Users
import repository.model.User
import repository.JwtUtils
import repository.PasswordEncode
import zio.{Task, ZIO}
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
  val app: HttpApp[Users, Response] =
    Http.collectZIO[Request] {
      case req @ Method.POST -> !! / "auth" / "login" => {
        (for {
          user <- req.body.asString.map(body => body.fromJson[UserCredentials]).right
          _ <- Users.login(User(user.login, PasswordEncode.encode(user.password)))
        } yield (Response.json(AuthorizationToken(JwtUtils.generateJwt(User(user.login, user.password))).toJson)))
          .orElseFail(Response.status(Status.BadRequest))
      }.orElseFail(Response.status(Status.BadRequest))
      case req@Method.POST -> !! / "auth" / "register" =>
        (for {
          user <- req.body.asString.map(body => body.fromJson[UserCredentials]).right
          _ <- Users.add(User(user.login, PasswordEncode.encode(user.password)))
        } yield(Response.status(Status.Created)))
          .orElseFail(Response.status(Status.BadRequest))
    }
}
