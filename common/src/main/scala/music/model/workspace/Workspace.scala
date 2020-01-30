package music.model.workspace

import music.model.workspace.User.UserId
import music.model.write.Track
import music.model.write.Track.TrackId

final case class Workspace(
  owner: UserId,
  selectedTrack: TrackId,
) {
  def selectTrack(track: Track): Workspace = copy(selectedTrack = track.id)
}

object Workspace {

  final case class WorkspaceId(id: Long) extends AnyVal

}