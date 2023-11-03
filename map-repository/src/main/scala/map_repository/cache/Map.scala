package map_repository.cache

import map_repository.db.{Edges, Points}
import map_repository.model.{Edge, Point}
import zio.{ZIO, ZLayer}

object MapInfo {
  trait Service {
    def getPoints: ZIO[Points, Throwable, Array[Point]]
    def getEdges: ZIO[Edges, Throwable, Array[Edge]]
  }

  object Service {
    def getPoints(): ZIO[Service with Edges with Points, Throwable, Array[Point]] =
      ZIO.serviceWithZIO[Service](_.getPoints)
    def getEdges(): ZIO[Service with Edges with Points, Throwable, Array[Edge]] =
      ZIO.serviceWithZIO[Service](_.getEdges)
  }

  val live: ZLayer[Any, Nothing, MapImpl] =
    ZLayer.fromFunction(() => new MapImpl())
}

class MapImpl extends MapInfo.Service {
  override def getPoints: ZIO[Points, Throwable, Array[Point]] = MapImpl.points
  override def getEdges: ZIO[Edges, Throwable, Array[Edge]] = MapImpl.edges
}

object MapImpl {
  private lazy val points: ZIO[Points, Throwable, Array[Point]] = {
    Points.getAll().runCollect.map(_.toArray)
  }
  private lazy val edges: ZIO[Edges, Throwable, Array[Edge]] = {
    Edges.getAll().runCollect.map(_.toArray)
  }
}
