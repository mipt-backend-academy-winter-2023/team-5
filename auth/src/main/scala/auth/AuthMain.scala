package auth

import api.HttpRoutes
import config.Config
import auth.config.ServiceConfig
import zio.http.Server
import flyway.FlywayAdapter
import zio.{Scope, ZIO, ZIOAppArgs, ZIOAppDefault, http}

object AuthMain extends ZIOAppDefault {
  override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] = {
    ZIO.service[FlywayAdapter.Service].flatMap(_.migration).provide(
      Config.dbLive,
      FlywayAdapter.live
    )
  }
}
