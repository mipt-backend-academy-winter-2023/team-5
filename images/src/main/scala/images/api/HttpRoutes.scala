package images.api

import zio.ZIO
import zio.http._
import zio.http.model.{Method, Status}
import zio.stream.{ZPipeline, ZSink, ZStream}

import java.nio.file.{Files, Paths}


object HttpRoutes {
  val app: HttpApp[Any, Response] =
    Http.collectZIO[Request] {
      case req@Method.PUT -> !! / "load"
        if req.headers.exists(_.key == "X-Node-Id") =>

        val idMapPoint = req.headers.find(_.key == "X-Node-Id").get.value
        val imagePath = Paths.get(s"./resources/${idMapPoint}")

        (for {
          _ <- ZIO
            .attempt(Files.createFile(imagePath))
            .either
            .map(_ => null)

          _ <- req.body.asStream
            .via(ZPipeline.deflate())
            .run(ZSink.fromPath(imagePath))
        } yield Response.status(Status.Created))
          .orElseFail(Response.status(Status.BadRequest))
      case req@Method.GET -> !! / "get" =>
        val nodeId = req.url.queryParams.get("nodeId")
        if (!nodeId.contains()) {
          Response.status(Status.BadRequest)
        }
        val imagePath = Paths.get(s"./resources/${nodeId.get}")
        if (!Files.exists(imagePath)) {
          Response.status(Status.NotFound)
        }

        ZIO.succeed(
          Response(
            body = Body.fromStream(
              ZStream
                .fromPath(imagePath)
                .via(ZPipeline.inflate())
            )
          )
        )
    }
}

