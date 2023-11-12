package auth

import repository.db.Users
import repository.model.User
import zio.ZIO
import zio.stream.ZStream
import scala.collection.mutable


final class MockUsers(users: mutable.Map[String, User])
  extends Users {
  override def add(user: User): ZIO[Users, Throwable, Unit] = {
    if (!users.contains(user.username)) {
      users += user.username -> user
      ZIO.succeed()
    } else {
      ZIO.fail(new Exception("Username is already taken"))
    }
  }

  override def findByUsername(user: User): ZStream[Users, Throwable, User] = {
    try {
      ZStream.fromZIO(ZIO.succeed(users(user.username)))
    } catch {
      case _: Throwable => ZStream.empty
    }
  }

  override def findByCredentials(user: User): ZStream[Any, Throwable, User] = {
    try {
      if (users(user.username).password_hash != user.password_hash) {
        throw new Exception("Wrong password")
      }
      ZStream.fromZIO(ZIO.succeed(user))
    } catch {
      case _: Throwable => ZStream.empty
    }
  }

  override def login(user: User): ZIO[Users, Throwable, Unit] = {
    findByCredentials(user).runCollect.map(_.toArray).either.flatMap {
      case Right(arr) =>
        arr match {
          case Array() =>
            ZIO.fail(new Exception("User not exists"))
          case Array(user) =>
            ZIO.succeed(true)
          case _ =>
            ZIO.fail(new Exception("Exists several users"))
        }
      case Left(e) =>
        ZIO.fail(e)
    }
  }
}