package server.storage.memory

import javax.inject.Singleton
import music.domain.user.User.UserId
import music.domain.user.{User, UserRepository}
import server.storage.EntityNotFoundException

import scala.util.{Failure, Success, Try}

@Singleton
class InMemoryUserRepository() extends UserRepository {

  private val users: Array[User] = Array[User](User(UserId(), "artamus"))

  def getByName(name: String): Try[User] = {
    users.find(_.name == name) match {
      case Some(user) => Success(user)
      case None => Failure(EntityNotFoundException("User"))
    }
  }

}