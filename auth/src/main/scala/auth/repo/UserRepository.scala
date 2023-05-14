package repo

import model.User
import zio.ZIO
import zio.stream.ZStream

trait UserRepository {

  def find(user: User): ZStream[Any, Throwable, User]

  def add(customer: User): ZIO[Any, Throwable, Unit]
}

object UserRepository {
  def find(user: User): ZStream[UserRepository, Throwable, User] =
    ZStream.serviceWithStream[UserRepository](_.find(user))

  def add(user: User): ZIO[UserRepository, Throwable, Unit] =
    ZIO.serviceWithZIO[UserRepository](_.add(user))
}