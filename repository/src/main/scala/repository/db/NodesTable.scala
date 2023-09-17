package repository.db

import repository.model.NodeRow
import zio.schema.DeriveSchema
import zio.sql.postgresql.PostgresJdbcModule

trait NodesTable extends PostgresJdbcModule{
  implicit val nodeSchema = DeriveSchema.gen[NodeRow]

  val nodesTable = defineTable[NodeRow]("nodes")

  val (fId, fName, fLatitude, fLongitude, fNodeType) = nodesTable.columns
}
