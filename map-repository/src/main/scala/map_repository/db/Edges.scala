package map_repository.db

import map_repository.model.Edge
import zio.schema.DeriveSchema
import zio.sql.ConnectionPool
import zio.sql.postgresql.PostgresJdbcModule
import zio.stream.ZStream
import zio.{ZIO, ZLayer}

trait Edges {
  def getAll(): ZStream[Edges, Throwable, Edge]
}

object Edges {
  def getAll(): ZStream[Edges, Throwable, Edge] =
    ZStream.serviceWithStream[Edges](_.getAll())
}

class EdgesTable extends PostgresJdbcModule {
  implicit val customerSchema = DeriveSchema.gen[Edge]

  val edgesTable = defineTable[Edge]("edges")

  val (id, name, x, y) = edgesTable.columns
}

class EdgesImpl(pool: ConnectionPool) extends EdgesTable with Edges {
  val driverLayer: ZLayer[Any, Nothing, SqlDriver] =
    ZLayer.make[SqlDriver](SqlDriver.live, ZLayer.succeed(pool))

  override def getAll(): ZStream[Edges, Throwable, Edge] = {
    val selectAll = select(id, name, x, y).from(edgesTable)
    ZStream.fromZIO(
      ZIO.logInfo(s"Query to execute findAll is ${renderRead(selectAll)}")
    ) *> execute(selectAll.to((Edge.apply _).tupled))
      .provideSomeLayer(driverLayer)
  }
}

object EdgesImpl {
  val live: ZLayer[ConnectionPool, Throwable, Edges] =
    ZLayer.fromFunction(new EdgesImpl(_))
}
