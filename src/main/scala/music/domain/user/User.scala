package music.domain.user

import music.domain.user.User.UserId
import music.domain.workspace.Workspace

trait User {
  val id: UserId
  val name: String
  def workspace: Workspace
}

object User {

  final case class UserId(id: Long) extends AnyVal

}
