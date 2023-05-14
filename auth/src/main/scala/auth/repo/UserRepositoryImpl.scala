package repo

import model.User
import zio.{ZIO, ZLayer}
import zio.sql.ConnectionPool
import zio.stream.ZStream

final class UserRepositoryImpl(
                                    pool: ConnectionPool
                                  ) extends UserRepository
  with PostgresTableDescription {

  val driverLayer: ZLayer[Any, Nothing, SqlDriver] =
    ZLayer
      .make[SqlDriver](
        SqlDriver.live,
        ZLayer.succeed(pool)
      )

  def EncryptPassword(password: String): String = {
    val crypt = java.security.MessageDigest.getInstance("SHA-1")
    crypt.reset()
    crypt.update(password.getBytes("UTF-8"))
    java.util.Base64.getEncoder.encodeToString(crypt.digest())
  }

  override def find(user: User): ZStream[Any, Throwable, User] = {
    val selectAll =
      select(fLogin, fPassword)
        .from(users)
        .where(fLogin === user.login && fPassword === EncryptPassword(user.password))

    ZStream.fromZIO(
      ZIO.logInfo(s"Query to execute findAll is ${renderRead(selectAll)}")
    ) *> execute(selectAll.to((User.apply _).tupled)).provideSomeLayer(driverLayer)
  }

  override def add(user: User): ZIO[Any, Throwable, Unit] = {
    val query =
      insertInto(users)(fLogin, fPassword)
        .values(
          (
            user.login,
            EncryptPassword(user.password)
          )
        )

    ZIO.logInfo(s"Query to insert user is ${renderInsert(query)}") *>
      execute(query)
        .provideSomeLayer(driverLayer)
        .unit
  }
}

object UserRepositoryImpl {
  val live: ZLayer[ConnectionPool, Throwable, UserRepository] =
    ZLayer.fromFunction(new UserRepositoryImpl(_))
}