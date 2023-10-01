package map_repository.flyway

import map_repository.MapDbConfig
import org.flywaydb.core.Flyway
import zio.{UIO, ZIO, ZLayer}

object FlywayAdapter {
  trait Service {
    def migration: UIO[Unit]
  }

  val live: ZLayer[MapDbConfig, Nothing, FlywayAdapterImpl] =
    ZLayer.fromFunction(new FlywayAdapterImpl(_))
}

class FlywayAdapterImpl(dbConfig: MapDbConfig) extends FlywayAdapter.Service {
  val flyway: UIO[Flyway] = ZIO
    .succeed(
      Flyway
        .configure()
        .locations("classpath:map/migration")
        .dataSource(dbConfig.url, dbConfig.user, dbConfig.password)
    )
    .map(new Flyway(_))

  override def migration: UIO[Unit] = flyway.map(_.migrate())

}
