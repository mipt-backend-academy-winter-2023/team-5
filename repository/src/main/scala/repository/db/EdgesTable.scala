package repository.db

import repository.model.EdgeRow
import zio.schema.DeriveSchema
import zio.sql.postgresql.PostgresJdbcModule

trait EdgesTable extends PostgresJdbcModule{
  implicit val EdgeSchema = DeriveSchema.gen[EdgeRow]

  val EdgesTable = defineTable[EdgeRow]("Edges")

  val (fId, fName, fFrom, fTo) = EdgesTable.columns
}
