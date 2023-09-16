package routing

import repository.Config
import repository.flyway.FlywayAdapter
import routing.api.HttpRoutes
import routing.config.ServiceConfig
import zio.http.Server
import zio.{Scope, ZIO, ZIOAppArgs, ZIOAppDefault}

object RoutingMain extends ZIOAppDefault {
  override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] = {
    val server =
      for {
        _ <- ZIO.logInfo("Start Routing")
        flyway <- ZIO.service[FlywayAdapter.Service]
        _ <- flyway.migration
        _ <- zio.http.Server.serve(HttpRoutes.app)
      } yield ()
    server.provide(
      Server.live,
      ServiceConfig.live,
      Config.dbLive,
      Config.connectionPoolLive,
      FlywayAdapter.live
    )
  }
}
