package music.domain.workspace

import music.domain.track.Track
import music.domain.user.User.UserId

trait Workspace {
  val owner: UserId
  def getEditedTrack: Track
  def startNewEdit: Workspace
}

object Workspace {

  final case class WorkspaceId(id: Long) extends AnyVal

}