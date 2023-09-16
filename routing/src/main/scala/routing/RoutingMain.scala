package routing

import api.HttpRoutes
import config.ServiceConfig
import repository.Config
import zio.http.Server
import zio.{Scope, ZIO, ZIOAppArgs, ZIOAppDefault}
import repository.flyway.FlywayAdapter

object RoutingMain extends ZIOAppDefault {
  override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] = {
    val server =
      for {
        _ <- ZIO.logInfo("Start Routing")
        flyway <- ZIO.service[FlywayAdapter.Service]
        _ <- flyway.migration
        _ <- zio.http.Server.serve(HttpRoutes.app)
      } yield()
    server.provide(
        Server.live,
        ServiceConfig.live,
        Config.dbLive,
        Config.connectionPoolLive,
        FlywayAdapter.live,
    )
  }
}
