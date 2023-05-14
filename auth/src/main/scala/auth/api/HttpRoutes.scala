package auth.api

import model.User
import model.JsonProtocol._
import zio.ZIO
import repo.UserRepository
import io.circe.jawn.decode
import io.circe.syntax.EncoderOps
import zio._
import zio.http._
import zio.http.model._
import zio.CanFail.canFailAmbiguous1
import zio.http.model.Status.{BadRequest, Created, Forbidden, Ok}

object HttpRoutes {
  val app: HttpApp[UserRepository, Response] =
    Http.collectZIO[Request] {
      case req@Method.POST -> !! / "auth" / "login" =>
        (for {
          bodyStr <- req.body.asString
          user <- ZIO.fromEither(decode[User](bodyStr)).tapError(e => ZIO.logError(e.getMessage))
          _ <- UserRepository.add(user)
          _ <- ZIO.logInfo(s"Created new user $user")
        } yield ()).either.map {
          case Right(_) => Response.status(Created)
          case Left(_) => Response.status(BadRequest)
        }

      case req@Method.POST -> !! / "auth" / "register" =>
        (for {
          bodyStr <- req.body.asString
          user <- ZIO.fromEither(decode[User](bodyStr)).tapError(e => ZIO.logError(e.getMessage))
          allFound <- UserRepository.find(user).runCollect.map(_.toArray)
        } yield (allFound)).either.map {
          case Right(users) =>
            users match {
              case Array() => Response.status(Forbidden)
              case htail => {
                ZIO.logInfo(s"Signed in ${htail.head}")
                Response.text(s"{\"token\": \"aaa\"}")
              }
            }
          case Left(_) => Response.status(BadRequest)
        }

      case Method.GET -> !! / "auth" / "test" =>
        ZIO.succeed(Response.status(Status.Ok))
    }
}
