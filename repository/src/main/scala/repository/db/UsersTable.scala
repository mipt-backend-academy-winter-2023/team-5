package repository.db

import repository.model.User
import zio.schema.DeriveSchema
import zio.sql.postgresql.PostgresJdbcModule

trait UsersTable extends PostgresJdbcModule{
  implicit val customerSchema = DeriveSchema.gen[User]

  val userTable = defineTable[User]("users")

  val (username, password_hash) = userTable.columns
}
