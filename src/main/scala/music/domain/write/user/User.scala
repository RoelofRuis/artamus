package music.domain.write.user

import java.util.UUID

import music.domain.write.user.User.UserId

final case class User(
  id: UserId,
  name: String,
)

object User {

  final case class UserId(id: UUID = UUID.randomUUID())

}
