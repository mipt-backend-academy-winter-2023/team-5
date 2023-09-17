package routing

import repository.Config
import repository.db.{EdgesImpl, Nodes, NodesImpl}
import repository.flyway.FlywayAdapter
import routing.api.HttpRoutes
import routing.config.ServiceConfig
import routing.graph.CityGraphImpl
import zio.http.Server
import zio.{Scope, ZIO, ZIOAppArgs, ZIOAppDefault}

object RoutingMain extends ZIOAppDefault {
  override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] = {
    val server =
      for {
        flyway <- ZIO.service[FlywayAdapter.Service]
        _ <- flyway.migration
        _ <- zio.http.Server.serve(HttpRoutes.app)
        _ <- CityGraphImpl.loadGraph()
      } yield ()
    server.provide(
      Server.live,
      ServiceConfig.live,
      Config.dbLive,
      Config.connectionPoolLive,
      zio.sql.ConnectionPool.live,
      FlywayAdapter.live,
      NodesImpl.live,
      EdgesImpl.live
    )
  }
}
