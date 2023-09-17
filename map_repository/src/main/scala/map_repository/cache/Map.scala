package map_repository.cache

import map_repository.db.Points
import zio.{UIO, ZLayer}

class Map {
  trait Service {
    def load: UIO[Unit]
  }

  val live: ZLayer[Points, Nothing, Map] =
    ZLayer.fromFunction(new MapImpl(_))
}

class MapImpl extends Map {}
