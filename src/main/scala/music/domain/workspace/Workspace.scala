package music.domain.workspace

import music.domain.track.Track
import music.domain.user.User.UserId

trait Workspace {
  val owner: UserId
  val editedTrack: Track
  val annotatedTrack: Option[Track]
  def startNewEdit: Workspace
  def makeEdit(track: Track): Workspace
  def makeAnnotations(track: Track): Workspace
  def useAnnotations: Workspace
}

object Workspace {

  final case class WorkspaceId(id: Long) extends AnyVal

}