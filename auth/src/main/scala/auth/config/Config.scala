package auth.config

import pureconfig._
import pureconfig.generic.semiauto.deriveReader
import zio.http.ServerConfig
import zio.sql.ConnectionPoolConfig
import zio.{ULayer, ZIO, ZLayer, http}

import java.util.Properties

object Config {
  private val source = ConfigSource.default.at("app")

  val dbLive: ULayer[DbConfig] = {
    import ConfigImpl._
    ZLayer.fromZIO(
      ZIO.attempt(source.loadOrThrow[ConfigImpl].dbConfig).orDie
    )
  }

  val connectionPoolConfigLive
  : ZLayer[DbConfig, Throwable, ConnectionPoolConfig] =
    ZLayer(
      ZIO.service[DbConfig].map { serverConfig =>
        val connPropertiesConfig = connProperties(serverConfig.user, serverConfig.password)
        ConnectionPoolConfig(serverConfig.url, connPropertiesConfig)
      }
    )

  private def connProperties(user: String, password: String): Properties = {
    val props = new Properties
    props.setProperty("user", user)
    props.setProperty("password", password)
    props
  }
}

case class ConfigImpl(
                       dbConfig: DbConfig,
                     )
case class DbConfig(
                     url: String,
                     user: String,
                     password: String
                   )

object ConfigImpl {
  implicit val configReader: ConfigReader[ConfigImpl] = deriveReader[ConfigImpl]
  implicit val configReaderDbConfig: ConfigReader[DbConfig] =
    deriveReader[DbConfig]
}