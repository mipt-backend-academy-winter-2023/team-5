package map_repository.cache

import map_repository.db.Points
import map_repository.model.Point
import zio.{ZIO, ZLayer}

object MapInfo {
  trait Service {
    def getPoints: ZIO[Points, Throwable, Array[Point]]
  }

  object Service {
    def getPoints(): ZIO[Service with Points, Throwable, Array[Point]] =
      ZIO.serviceWithZIO[Service](_.getPoints)
  }

  val live: ZLayer[Any, Nothing, MapImpl] =
    ZLayer.fromFunction(() => new MapImpl())
}

class MapImpl extends MapInfo.Service {
  override def getPoints: ZIO[Points, Throwable, Array[Point]] = MapImpl.points
}

object MapImpl {
  private lazy val points: ZIO[Points, Throwable, Array[Point]] = {
    Points.getAll().runCollect.map(_.toArray)
  }
}
