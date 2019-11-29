package server.storage

import javax.inject.{Inject, Singleton}
import music.domain.user.{User, UserRepository}
import music.domain.user.User.UserId

import scala.util.{Failure, Success, Try}

@Singleton
class InMemoryUserRepository() extends UserRepository {

  private val users: Array[User] = Array[User](User(UserId(0), "artamus"))

  def getByName(name: String): Try[User] = {
    users.find(_.name == name) match {
      case Some(user) => Success(user)
      case None => Failure(EntityNotFoundException("User"))
    }
  }

}