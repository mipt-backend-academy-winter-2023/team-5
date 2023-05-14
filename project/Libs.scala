import sbt._

object V {
  val circe_version = "0.14.1"
  val flyway = "9.16.0"
  val pdiJwt = "9.2.0"
  val pureconfig = "0.17.2"
  val zio = "2.0.13"
  val zioHttp = "0.0.5"
  val zio_sql = "0.1.2"
}


object Libs {

  val circe: List[ModuleID] = List(
    "io.circe" %% "circe-core",
    "io.circe" %% "circe-generic",
    "io.circe" %% "circe-parser"
  ).map(_ % V.circe_version)

  val pdiJwt: List[ModuleID] = List(
    "com.github.jwt-scala" %% "jwt-core" % V.pdiJwt
  )

  val pureconfig: List[ModuleID] = List(
    "com.github.pureconfig" %% "pureconfig" % V.pureconfig
  )

  val flyway: List[ModuleID] = List(
    "org.flywaydb" % "flyway-core" % V.flyway
  )

  val zio: List[ModuleID] = List(
    "dev.zio" %% "zio" % V.zio,
    "dev.zio" %% "zio-http" % V.zioHttp,
    "dev.zio" %% "zio-sql-postgres" % V.zio_sql
  )

}
