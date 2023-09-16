import Dependencies.*

ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.10"

lazy val root = (project in file("."))
  .settings(
    name := "project-mipt"
  )
  .aggregate(
    auth,
    routing,
    helper
  )
  .dependsOn(
    auth,
    routing,
    helper
  )

lazy val auth = (project in file("auth"))
  .settings(
    name := "project-auth",
    libraryDependencies ++= Auth.dependencies
  )
  .aggregate(repository)
  .dependsOn(repository)

lazy val routing = (project in file("routing"))
  .settings(
    name := "project-routing",
    libraryDependencies ++= Routing.dependencies
  )
  .aggregate(repository)
  .dependsOn(repository, map_repository)

lazy val helper = (project in file("helper"))
  .settings(
    name := "project-helper",
    libraryDependencies ++= Helper.dependencies
  )

lazy val repository = (project in file("repository"))
  .settings(
    name := "project-repository",
    libraryDependencies ++= Repository.dependencies
  )

lazy val map_repository = (project in file("map_repository"))
  .settings(
    name := "map_repository",
    libraryDependencies ++= MapRepository.dependencies
  )

ThisBuild / assemblyMergeStrategy := {
  case PathList("META-INF", xs @ _*) =>
    (xs map {
      _.toLowerCase
    }) match {
      case ("manifest.mf" :: Nil) | ("index.list" :: Nil) |
          ("dependencies" :: Nil) =>
        MergeStrategy.discard
      case ps @ (x :: xs)
          if ps.last.endsWith(".sf") || ps.last.endsWith(".dsa") =>
        MergeStrategy.discard
      case "plexus" :: xs =>
        MergeStrategy.discard
      case "services" :: xs =>
        MergeStrategy.filterDistinctLines
      case ("spring.schemas" :: Nil) | ("spring.handlers" :: Nil) =>
        MergeStrategy.filterDistinctLines
      case _ => MergeStrategy.first
    }
  case x => (ThisBuild / assemblyMergeStrategy).value(x)
}
