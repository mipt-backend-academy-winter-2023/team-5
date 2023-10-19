package images.api

import zio.ZIO
import zio.http._
import zio.http.model.{Method, Status}
import zio.stream.{ZPipeline, ZSink, ZStream}

import java.nio.file.{Files, Paths}


object HttpRoutes {
  val app: HttpApp[Any, Response] =
    Http.collectZIO[Request] {
      case req@Method.POST -> !! / "load" / nodeId =>

        val imagePath = Paths.get(s"./images_sources/$nodeId")

        (for {
          _ <- ZIO
            .attempt(Files.createFile(imagePath))
            .either
            .map(_ => null)

          _ <- req.body.asStream
            .via(ZPipeline.deflate())
            .run(ZSink.fromPath(imagePath))
        } yield Response.status(Status.Created))
          .orElseFail(Response.status(Status.InternalServerError))
      case req@Method.GET -> !! / "get" / nodeId =>
        val imagePath = Paths.get(s"./images_sources/${nodeId}")
        (for {
          response <-
            if (!Files.exists(imagePath)) {
              ZIO.fail("No such file")
            } else {
              ZIO.succeed(
                Response(body = Body.fromStream(
                  ZStream
                    .fromPath(imagePath)
                    .via(ZPipeline.inflate())
                ))
              )
            }
        } yield response).either.map {
          case Right(response) => response
          case Left(_) => Response.status(Status.BadRequest)
        }
    }
}

