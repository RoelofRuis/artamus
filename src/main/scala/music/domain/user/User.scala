package music.domain.user

import music.domain.user.User.UserId

trait User {
  val id: UserId
  val name: String
}

object User {

  final case class UserId(id: Long) extends AnyVal

}
