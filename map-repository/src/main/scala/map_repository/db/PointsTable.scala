package map_repository.db

import map_repository.model.Point
import zio.schema.DeriveSchema
import zio.sql.postgresql.PostgresJdbcModule

class PointsTable extends PostgresJdbcModule {
  implicit val customerSchema = DeriveSchema.gen[Point]

  val pointsTable = defineTable[Point]("points")

  val (id, name, x, y) = pointsTable.columns
}
