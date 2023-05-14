package repository.db

import repository.model.User
import zio.{Task, ZIO}
import zio.stream.ZStream

trait Users {
  def findByCredentials(user: User): ZStream[Users, Throwable, User]

  def findByUsername(user: User): ZStream[Users, Throwable, User]

  def add(user: User): ZIO[Users, Throwable, Unit]

  def login(user: User): ZIO[Users, Throwable, Unit]
}

object Users {
  def findByCredentials(user: User): ZStream[Users, Throwable, User] =
    ZStream.serviceWithStream[Users](_.findByCredentials(user))

  def findByUsername(user: User): ZStream[Users, Throwable, User] =
    ZStream.serviceWithStream[Users](_.findByCredentials(user))

  def add(user: User): ZIO[Users, Throwable, Unit] =
    ZIO.serviceWithZIO[Users](_.add(user))

  def login(user: User): ZIO[Users, Throwable, Unit] =
    ZIO.serviceWithZIO[Users](_.login(user))
}