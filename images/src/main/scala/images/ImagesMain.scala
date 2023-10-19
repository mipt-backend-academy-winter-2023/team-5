package images

import repository.Config
import repository.flyway.FlywayAdapter
import images.api.HttpRoutes
import images.config.ServiceConfig
import zio.http.Server
import zio.{Scope, ZIO, ZIOAppArgs, ZIOAppDefault}

object ImagesMain extends ZIOAppDefault {
  override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] = {
    val server =
      for {
        flyway <- ZIO.service[FlywayAdapter.Service]
        _ <- flyway.migration
        _ <- zio.http.Server.serve(HttpRoutes.app)
      } yield ()
    server.provide(
      Server.live,
      ServiceConfig.live,
      Config.dbLive,
      FlywayAdapter.live
    )
  }
}
