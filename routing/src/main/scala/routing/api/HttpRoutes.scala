package routing.api

import zio.http._
import zio.http.model._
import zio.ZIO

object HttpRoutes {
  val app: HttpApp[Any, Response] =
    Http.collectZIO[Request] {
      case Method.POST -> !! / "route" / "search" =>
        ZIO.succeed(Response.status(Status.Ok))
    }

}
