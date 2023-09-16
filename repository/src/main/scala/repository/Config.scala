package repository

import pureconfig.ConfigSource
import pureconfig.generic.auto.exportReader
import zio.sql.ConnectionPoolConfig
import zio.{ULayer, ZIO, ZLayer}

import java.util.Properties

case class DbConfig(url: String, user: String, password: String)

object Config {
  private val source = ConfigSource.default.at("app").at("biba")

  val dbLive: ULayer[DbConfig] = {
    ZLayer.fromZIO(
      ZIO.attempt(source.loadOrThrow[DbConfig]).orDie
    )
  }

  val connectionPoolLive: ZLayer[DbConfig, Throwable, ConnectionPoolConfig] =
    ZLayer(
      ZIO
        .service[DbConfig]
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
