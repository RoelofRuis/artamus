package music.domain.workspace

import music.domain.track.Track
import music.domain.user.User.UserId

trait Workspace {
  val owner: UserId
  val editedTrack: Track
  def startNewEdit: Workspace
  def makeEdit(track: Track): Workspace
}

object Workspace {

  final case class WorkspaceId(id: Long) extends AnyVal

}