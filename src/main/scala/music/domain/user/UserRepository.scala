package music.domain.user

import javax.inject.{Inject, Singleton}
import music.domain.user.User.UserId
import music.domain.user.UserRepository.UserImpl

@Singleton
class UserRepository @Inject() () {

  private val users: Array[User] = Array[User](UserImpl(UserId(0), "artamus"))

  def getByName(name: String): Option[User] = users.find(_.name == name)

}

object UserRepository {

  final case class UserImpl(
    id: UserId,
    name: String,
  ) extends User

}