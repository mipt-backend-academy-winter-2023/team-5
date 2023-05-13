package auth.api

import zio.http._
import zio.http.model._
import zio.ZIO

object HttpRoutes {
  val app: HttpApp[Any, Response] =
    Http.collectZIO[Request] {
      case Method.POST -> !! / "auth" / "login" =>
        ZIO.succeed(Response.status(Status.Ok))
      case Method.POST -> !! / "auth" / "register" =>
        ZIO.succeed(Response.status(Status.Ok))
    }
}
