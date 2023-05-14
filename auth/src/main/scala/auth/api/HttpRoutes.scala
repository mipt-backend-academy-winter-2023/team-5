package auth.api

import model.User
import model.JsonProtocol._
import zio.ZIO
import repo.UserRepository
import io.circe.jawn.decode
import zio.http._
import zio.http.model._
import zio.http.model.Status.{BadRequest, Created, Forbidden, Ok}
import pdi.jwt.{JwtJson4s, JwtAlgorithm}, org.json4s._, org.json4s.JsonDSL.WithBigDecimal._


object HttpRoutes {
  private def generateJwtToken(login: String): String = {
    val key = "secretKey"
    val claim = JObject(("login", login), ("key", key))
    val algo = JwtAlgorithm.HS256
    JwtJson4s.encode(claim)
    val token = JwtJson4s.encode(claim, key, algo)
    token
  }

  val app: HttpApp[UserRepository, Response] =
    Http.collectZIO[Request] {
      case req@Method.POST -> !! / "auth" / "register" =>
        (for {
          bodyStr <- req.body.asString
          user <- ZIO.fromEither(decode[User](bodyStr)).tapError(e => ZIO.logError(e.getMessage))
          _ <- UserRepository.add(user)
          _ <- ZIO.logInfo(s"Register $user")
        } yield ()).either.map {
          case Right(_) => Response.status(Created)
          case Left(_) => Response.status(BadRequest)
        }

      case req@Method.POST -> !! / "auth" / "login" =>
        (for {
          bodyStr <- req.body.asString
          user <- ZIO.fromEither(decode[User](bodyStr)).tapError(e => ZIO.logError(e.getMessage))
          allFound <- UserRepository.find(user).runCollect.map(_.toArray)
        } yield allFound).either.map {
          case Right(users) =>
            users match {
              case Array() => Response.status(Forbidden)
              case arr =>
                ZIO.logInfo(s"Login ${arr.head}")
                Response.text(s"{\"token\": \"${generateJwtToken(arr.head.login)}\"}")
            }
          case Left(_) => Response.status(BadRequest)
        }

      case Method.GET -> !! / "auth" / "test" =>
        ZIO.succeed(Response.status(Ok))
    }
}
