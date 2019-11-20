package music.domain.workspace

import music.domain.user.User.UserId

final case class Workspace(
  owner: UserId
)

object Workspace {

  final case class WorkspaceId(id: Long) extends AnyVal

}