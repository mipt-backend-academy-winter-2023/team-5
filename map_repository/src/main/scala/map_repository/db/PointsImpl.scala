package map_repository.db
import map_repository.model.Point
import zio.sql.ConnectionPool
import zio.stream.ZStream
import zio.{ZIO, ZLayer}

class PointsImpl(pool: ConnectionPool) extends PointsTable with Points {
  val driverLayer: ZLayer[Any, Nothing, SqlDriver] =
    ZLayer.make[SqlDriver](SqlDriver.live, ZLayer.succeed(pool))

  override def getAll(): ZStream[Points, Throwable, Point] = {
    println("get all")
    val selectAll = select(id, name, x, y).from(pointsTable)
    ZStream.fromZIO(
      ZIO.logInfo(s"Query to execute findAll is ${renderRead(selectAll)}")
    ) *> execute(selectAll.to((Point.apply _).tupled))
      .provideSomeLayer(driverLayer)
  }
}

object PointsImpl {
  val live: ZLayer[ConnectionPool, Throwable, Points] =
    ZLayer.fromFunction(new PointsImpl(_))
}
