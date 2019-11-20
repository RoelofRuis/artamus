package music.domain.user

import music.domain.user.User.UserId

class UserRepository {

  private val users: Array[User] = Array[User](User(UserId(0), "artamus"))

  def getByName(name: String): Option[User] = users.find(_.name == name)

}
