package nl.roelofruis.artamus.core.model.workspace

import java.util.UUID

import nl.roelofruis.artamus.core.model.workspace.User.UserId

final case class User(
  id: UserId,
  name: String,
)

object User {

  final case class UserId(id: UUID = UUID.randomUUID())

}
