package map_repository.db

import map_repository.model.Point
import zio.stream.ZStream

trait Points {
  def getAll(): ZStream[Points, Throwable, Point]
}

object Points {
  def getAll(): ZStream[Points, Throwable, Point] =
    ZStream.serviceWithStream[Points](_.getAll())
}
