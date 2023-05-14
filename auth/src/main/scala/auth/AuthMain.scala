package auth

import api.HttpRoutes
import auth.config.ServiceConfig
import repository.db.UsersImpl
import repository.flyway.FlywayAdapter
import zio.http.Server
import zio.{Scope, ZIO, ZIOAppArgs, ZIOAppDefault, http}
import repository.{Config, Repository}
import zio.sql.ConnectionPool

object AuthMain extends ZIOAppDefault {
  override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] = {
    println("Hello world 1")
    val server =
      for {
        flyway <- ZIO.service[FlywayAdapter.Service]
        _ <- flyway.migration
        _ <- zio.http.Server.serve(HttpRoutes.app)
      } yield()
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
