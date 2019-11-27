package music.domain.workspace

import music.domain.track.Track
import music.domain.track.Track.TrackId
import music.domain.user.User.UserId

final case class Workspace(
  owner: UserId,
  editedTrack: Option[TrackId],
  annotatedTrack: Option[TrackId]
) {
  def startNewEdit: Workspace = copy(editedTrack = None)
  def setTrackToEdit(track: Track): Workspace = copy(editedTrack = track.id)
  def setTrackToAnnotate(track: Track): Workspace = copy(annotatedTrack = track.id)
  def useAnnotations: Workspace = {
    annotatedTrack match {
      case None => this
      case annotatedTrackId @ Some(trackId) => copy(editedTrack = annotatedTrackId)
    }
  }
}

object Workspace {

  final case class WorkspaceId(id: Long) extends AnyVal

}