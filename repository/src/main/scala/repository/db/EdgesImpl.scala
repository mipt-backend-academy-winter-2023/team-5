package repository.db

import repository.model.EdgeRow
import repository.db.EdgesTable
import zio.{ZIO, ZLayer}
import zio.sql.ConnectionPool
import zio.stream.ZStream

final class EdgesImpl(pool: ConnectionPool) extends EdgesTable with Edges {
  val driverLayer: ZLayer[Any, Nothing, SqlDriver] =
    ZLayer.make[SqlDriver](SqlDriver.live, ZLayer.succeed(pool))

  override def getAll(): ZStream[Any, Throwable, EdgeRow] = {
    val selectAll = select(fId, fName, fFrom, fTo)
      .from(EdgesTable)
    ZStream.fromZIO(
      ZIO.logInfo(s"Query to execute getAll is ${renderRead(selectAll)}")
    ) *> execute(selectAll.to((EdgeRow.apply _).tupled)).provideSomeLayer(driverLayer)
  }

}

object EdgesImpl {
  val live: ZLayer[ConnectionPool, Throwable, Edges] = ZLayer.fromFunction(new EdgesImpl(_))
}
