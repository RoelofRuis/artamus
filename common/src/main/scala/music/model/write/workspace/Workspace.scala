package music.model.write.workspace

import music.model.write.track.Track
import music.model.write.track.Track.TrackId
import music.model.write.user.User.UserId

final case class Workspace(
  owner: UserId,
  selectedTrack: TrackId,
) {
  def selectTrack(track: Track): Workspace = copy(selectedTrack = track.id)
}

object Workspace {

  final case class WorkspaceId(id: Long) extends AnyVal

}