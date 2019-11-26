package music.domain.workspace

import music.domain.track.Track
import music.domain.user.User.UserId

final case class Workspace(
  owner: UserId,
  editedTrack: Track,
  annotatedTrack: Option[Track] = None
) {
  def startNewEdit: Workspace = copy(editedTrack = Track())
  def makeEdit(track: Track): Workspace = copy(editedTrack = track)
  def makeAnnotations(track: Track): Workspace = copy(annotatedTrack = Some(track))
  def useAnnotations: Workspace = {
    annotatedTrack match {
      case None => this
      case Some(annotated) => copy(editedTrack = annotated)
    }
  }
}

object Workspace {

  final case class WorkspaceId(id: Long) extends AnyVal

}