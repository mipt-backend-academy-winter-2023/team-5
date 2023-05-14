package repository.db

import repository.model.User
import repository.db.UsersTable
import zio.{ZIO, ZLayer}
import zio.sql.ConnectionPool
import zio.stream.ZStream

final class UsersImpl(pool: ConnectionPool) extends UsersTable with Users {
  val driverLayer: ZLayer[Any, Nothing, SqlDriver] =
    ZLayer.make[SqlDriver](SqlDriver.live, ZLayer.succeed(pool))

  override def findByUsername(user: User): ZStream[Any, Throwable, User] = {
    val selectAll = select(username, password_hash)
      .from(userTable)
      .where(username === user.username)
    ZStream.fromZIO(
      ZIO.logInfo(s"Query to execute findAll is ${renderRead(selectAll)}")
    ) *> execute(selectAll.to((User.apply _).tupled)).provideSomeLayer(driverLayer)
  }

  override def findByCredentials(user: User): ZStream[Any, Throwable, User] = {
    val selectAll = select(username, password_hash)
      .from(userTable)
      .where(username === user.username && password_hash === user.password_hash)
    ZStream.fromZIO(
      ZIO.logInfo(s"Query to execute findAll is ${renderRead(selectAll)}")
    ) *> execute(selectAll.to((User.apply _).tupled)).provideSomeLayer(driverLayer)
  }

  override def add(user: User): ZIO[Users, Throwable, Unit] = {
    findByUsername(user).runCollect.map(_.toArray).either.flatMap {
      case Right(arr) => arr match {
        case Array() =>
          val query =
            insertInto(userTable)(username, password_hash)
              .values(
                (
                  user.username,
                  user.password_hash
                )
              )
          ZIO.logInfo(s"Query to insert user is ${renderInsert(query)}") *>
            execute(query).provideSomeLayer(driverLayer).unit
        case _ => ZIO.fail(new Exception("User exists"))
      }
      case Left(e) => {
        ZIO.fail(e)
      }
    }
  }

  override def login(user: User): ZIO[Users, Throwable, Unit] = {
    findByCredentials(user).runCollect.map(_.toArray).either.flatMap {
      case Right(arr) => arr match {
        case Array() =>
          ZIO.fail(new Exception("User exists"))
        case _ => ZIO.succeed(true)
      }
      case Left(e) =>
        ZIO.fail(e)
    }
  }
}

object UsersImpl {
  val live: ZLayer[ConnectionPool, Throwable, Users] = ZLayer.fromFunction(new UsersImpl(_))
}
