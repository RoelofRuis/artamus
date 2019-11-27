package server.storage

import javax.inject.{Inject, Singleton}
import music.domain.user.{User, UserRepository}
import music.domain.user.User.UserId

import scala.util.{Success, Try}

@Singleton
class InMemoryUserRepository @Inject() () extends UserRepository {

  private val users: Array[User] = Array[User](User(UserId(0), "artamus"))

  def getByName(name: String): Try[Option[User]] = Success(users.find(_.name == name))

}