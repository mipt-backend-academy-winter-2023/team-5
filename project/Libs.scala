import sbt.*

object V {
  val zio = "2.0.13"
  val zioHttp = "0.0.5"
  val zioSqlPostgres = "0.1.2"

  val flyway = "9.16.0"
  val circe_version = "0.14.1"
  val pdiJwt = "9.2.0"
  val pureconfig = "0.17.3"

  val postgres = "42.5.4"

  val http4s_dsl = "0.23.14"
  val http4s_blaze_server = "0.23.14"
  val http4s_blaze_client = "0.23.14"

  val sttp = "3.9.0"
  val rezilience = "0.9.3"
}

object Libs {

  val zio: List[ModuleID] = List(
    "dev.zio" %% "zio" % V.zio,
    "dev.zio" %% "zio-http" % V.zioHttp,
    "dev.zio" %% "zio-sql-postgres" % V.zioSqlPostgres,
    "dev.zio" %% "zio-test" % V.zio % "test",
    "dev.zio" %% "zio-test-sbt" % V.zio % "test",
    "org.http4s" %% "http4s-dsl" % V.http4s_dsl,
    "org.http4s" %% "http4s-blaze-server" % V.http4s_blaze_server,
    "org.http4s" %% "http4s-blaze-client" % V.http4s_blaze_client
  )

  val pureconfig: List[ModuleID] = List(
    "com.github.pureconfig" %% "pureconfig" % V.pureconfig
  )

  val flyway: List[ModuleID] = List(
    "org.flywaydb" % "flyway-core" % V.flyway
  )

  val circe: List[ModuleID] = List(
    "io.circe" %% "circe-core",
    "io.circe" %% "circe-generic",
    "io.circe" %% "circe-parser"
  ).map(_ % V.circe_version)

  val pdiJwt: List[ModuleID] = List(
    "com.github.jwt-scala" %% "jwt-core" % V.pdiJwt
  )

  val postgres: List[ModuleID] = List(
    "org.postgresql" % "postgresql" % V.postgres
  )

  val sttp: List[ModuleID] = List(
    "com.softwaremill.sttp.client3" %% "zio" % V.sttp,
    "com.softwaremill.sttp.client3" %% "circe" % V.sttp
  )

  val rezilience = List(
    "nl.vroste" %% "rezilience" % V.rezilience
  )
}
