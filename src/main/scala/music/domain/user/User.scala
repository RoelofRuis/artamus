package music.domain.user

import music.domain.user.User.UserId

final case class User(
  id: UserId,
  name: String
)

object User {

  final case class UserId(id: Long) extends AnyVal

}


