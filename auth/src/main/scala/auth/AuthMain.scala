package auth

import api.HttpRoutes
import config.Config
import auth.config.ServiceConfig
import zio.http.Server
import flyway.FlywayAdapter
import zio.{Scope, ZIO, ZIOAppArgs, ZIOAppDefault, http}
import zio.sql.ConnectionPool
import repo.UserRepositoryImpl

object AuthMain extends ZIOAppDefault {
  override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] = {
    val server =
      for {
        flyway <- ZIO.service[FlywayAdapter.Service]
        _ <- flyway.migration
        server <- zio.http.Server.serve(HttpRoutes.app)
      } yield ()
    server.provide(
      Server.live,
      ServiceConfig.live,
      Config.dbLive,
      FlywayAdapter.live,
      Config.connectionPoolConfigLive,
      ConnectionPool.live,
      UserRepositoryImpl.live
    )
  }
}
