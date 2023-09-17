package repository.db

import repository.model.NodeRow
import repository.db.NodesTable
import zio.{ZIO, ZLayer}
import zio.sql.ConnectionPool
import zio.stream.ZStream

final class NodesImpl(pool: ConnectionPool) extends NodesTable with Nodes {
  val driverLayer: ZLayer[Any, Nothing, SqlDriver] =
    ZLayer.make[SqlDriver](SqlDriver.live, ZLayer.succeed(pool))

  override def getAll(): ZStream[Any, Throwable, NodeRow] = {
    val selectAll = select(fId, fName, fLatitude, fLongitude, fNodeType)
      .from(nodesTable)
      .where(fId > 0)
    ZStream.fromZIO(
      ZIO.logInfo(s"Query to execute getAll is ${renderRead(selectAll)}")
    ) *> execute(selectAll.to((NodeRow.apply _).tupled)).provideSomeLayer(driverLayer)
  }

}

object NodesImpl {
  val live: ZLayer[ConnectionPool, Throwable, Nodes] = ZLayer.fromFunction(new NodesImpl(_))
}
