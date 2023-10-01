package map_repository

import pureconfig.ConfigSource
import pureconfig.generic.auto.exportReader
import zio.sql.ConnectionPoolConfig
import zio.{ULayer, ZIO, ZLayer}

import java.util.Properties

case class MapDbConfig(
    url: String,
    user: String,
    password: String
)
object Config {
  private val source = ConfigSource.default.at("app").at("map")

  val dbLive: ULayer[MapDbConfig] = {
    ZLayer.fromZIO(
      ZIO.attempt(source.loadOrThrow[MapDbConfig]).orDie
    )
  }

  val connectionPoolLive: ZLayer[MapDbConfig, Throwable, ConnectionPoolConfig] =
    ZLayer(
      ZIO
        .service[MapDbConfig]
        .map(serverConfig =>
          ConnectionPoolConfig(
            serverConfig.url,
            connProperties(serverConfig.user, serverConfig.password)
          )
        )
    )

  private def connProperties(user: String, password: String): Properties = {
    val props = new Properties
    props.setProperty("user", user)
    props.setProperty("password", password)
    props
  }
}
