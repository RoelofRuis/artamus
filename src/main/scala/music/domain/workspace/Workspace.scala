package music.domain.workspace

import music.domain.track.Track
import music.domain.track.Track.TrackId
import music.domain.user.User.UserId

final case class Workspace(
  owner: UserId,
  editedTrack: Option[TrackId]
) {
  def startNewEdit: Workspace = copy(editedTrack = None)
  def setTrackToEdit(track: Track): Workspace = copy(editedTrack = track.id)
}

object Workspace {

  final case class WorkspaceId(id: Long) extends AnyVal

}