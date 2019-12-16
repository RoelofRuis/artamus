package music.domain.write.workspace

import music.domain.write.track.Track
import music.domain.write.track.Track.TrackId
import music.domain.write.user.User.UserId

final case class Workspace(
  owner: UserId,
  editedTrack: TrackId
) {
  def setTrackToEdit(track: Track): Workspace = copy(editedTrack = track.id)
}

object Workspace {

  final case class WorkspaceId(id: Long) extends AnyVal

}