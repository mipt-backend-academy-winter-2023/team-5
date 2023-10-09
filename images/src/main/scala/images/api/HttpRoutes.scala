package images.api

import zio.ZIO
import zio.http._
import zio.http.model.{Method, Status}
import zio.stream.{ZPipeline, ZSink, ZStream}
import org.http4s.headers

import java.nio.file.{Files, Paths}


object HttpRoutes {
  val MaxSize: Long = 10L * 1024L * 1024L // 10 MB
  val DestinationDir: String = "imgs"

  val app: HttpApp[Any, Response] =
    Http.collectZIO[Request] {
      case req @ Method.POST -> !! / "upload" / nodeId
          if req.contentType.contains(
            headers.`Content-Type`(org.http4s.MediaType.image.jpeg)
          ) && req.contentLength.getOrElse(0L) <= MaxSize =>
        (for {
          path <- ZIO.attempt(
            Files.createFile(Paths.get(DestinationDir, nodeId))
          )
          _ <- req.body.asStream
            .via(ZPipeline.deflate())
            .run(ZSink.fromPath(path))
        } yield (Response.status(Status.Ok)))
          .orElseFail(Response.status(Status.BadRequest))
      case req@Method.GET -> !! / "download" / nodeId =>
        val imagePath = Paths.get(DestinationDir, nodeId)

        if (!Files.exists(imagePath)) {
          Response.status(Status.NotFound)
        }

        val bodyStream = ZStream
          .fromPath(imagePath)
          .via(ZPipeline.inflate())

        ZIO.succeed(Response(body = Body.fromStream(bodyStream)))
    }
}

