package map_repository.db

import map_repository.model.Point
import zio.ZIO
import zio.stream.ZStream

trait Points {
  def getAll(): ZStream[Points, Throwable, Point]

  def foo(): ZIO[Points, Throwable, Unit]
}

object Points {
  def getAll(): ZStream[Points, Throwable, Point] =
    ZStream.serviceWithStream[Points](_.getAll())

  def foo(): ZIO[Points, Throwable, Unit] =
    ZIO.serviceWithZIO[Points](_.foo())
}
