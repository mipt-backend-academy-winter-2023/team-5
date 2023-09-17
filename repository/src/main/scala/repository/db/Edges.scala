package repository.db

import repository.model.EdgeRow
import zio.stream.ZStream

trait Edges {
  def getAll(): ZStream[Edges, Throwable, EdgeRow]
}

object Edges {
  def getAll(): ZStream[Edges, Throwable, EdgeRow] = ZStream.serviceWithStream[Edges](_.getAll())
}
