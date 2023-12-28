package auth

import auth.api.HttpRoutes
import repository.db.Users
import repository.model.User
import io.circe.Json
import io.circe.Encoder
import io.circe.generic.codec.DerivedAsObjectCodec.deriveCodec
import zio.http.{!!, Body, Request, Response, URL}
import zio.{Scope, ULayer, ZIO, ZLayer}
import zio.http.model.Status
import zio.test.{Spec, TestEnvironment, ZIOSpecDefault, assertTrue}
import zio.test._
import zio.test.Assertion._

import scala.collection.mutable

trait SpecBase {
  val user1: User = User("user1", "***")

  def userBody(user: User): Body = {
    val json = Json.obj(
      "login" -> Encoder[String].apply(user.username),
      "password" -> Encoder[String].apply(user.password_hash)
    )
    Body.fromString(json.noSpaces)
  }

  def getMockUsers(): ULayer[MockUsers] = {
    ZLayer.succeed(new MockUsers(mutable.HashMap.empty))
  }

  def checkStatus(response: Response, status: Status): Boolean =
    response.status == status

  def register(user: User): ZIO[Users, Option[Response], Response] =
    HttpRoutes.app.runZIO(
      Request.post(
        userBody(user),
        URL(!! / "auth" / "register")
      )
    )

  def login(user: User): ZIO[Users, Option[Response], Response] =
    HttpRoutes.app.runZIO(
      Request.post(
        userBody(user),
        URL(!! / "auth" / "login")
      )
    )
}

object TestRegister extends ZIOSpecDefault with SpecBase {
  def spec: Spec[TestEnvironment with Scope, Any] = suite("TestRegister")(
    test("Register") {
      (for {
        user_register <- register(user1)
      } yield {
        assertTrue(checkStatus(user_register, Status.Created))
      }).provideLayer(
        getMockUsers()
      )
    },
    test("RegisterFail") {
      (for {
        _ <- register(user1)
        same_user <- register(user1).exit
      } yield {
        assert(same_user)(
          fails(
            equalTo(
              Some(
                Response.status(Status.BadRequest)
              )
            )
          )
        )
      }).provideLayer(
        getMockUsers()
      )
    }
  )
}

object TestLogin extends ZIOSpecDefault with SpecBase {
  def spec: Spec[TestEnvironment with Scope, Any] = suite("TestLogin")(
    test("Login") {
      (for {
        user_register <- register(user1)
        user_login1 <- login(user1)
      } yield {
        assertTrue(
          checkStatus(user_register, Status.Created)
            && checkStatus(user_login1, Status.Ok)
        )
      }).provideLayer(
        getMockUsers()
      )
    },
    test("LoginFailed") {
      (for {
        user_login <- login(user1).exit
      } yield {
        assert(user_login)(
          fails(
            equalTo(
              Some(
                Response.status(Status.BadRequest)
              )
            )
          )
        )
      }).provideLayer(
        getMockUsers()
      )
    }
  )
}
