package music.domain.user

import javax.inject.{Inject, Singleton}
import music.domain.user.User.UserId

@Singleton
class UserRepository @Inject() () {

  private val users: Array[User] = Array[User](User(UserId(0), "artamus"))

  def getByName(name: String): Option[User] = users.find(_.name == name)

}
