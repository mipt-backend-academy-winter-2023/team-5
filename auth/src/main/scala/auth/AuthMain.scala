package auth

import auth.api.HttpRoutes
import auth.config.ServiceConfig
import repository.Config
import repository.db.UsersImpl
import repository.flyway.FlywayAdapter
import zio.http.Server
import zio.{Scope, ZIO, ZIOAppArgs, ZIOAppDefault}

object AuthMain extends ZIOAppDefault {
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
      Config.connectionPoolLive,
      zio.sql.ConnectionPool.live,
      FlywayAdapter.live,
      UsersImpl.live
    )
  }
}
