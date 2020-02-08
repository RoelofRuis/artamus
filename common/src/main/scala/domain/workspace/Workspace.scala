package domain.workspace

import domain.workspace.User.UserId
import domain.write.Track
import domain.write.Track.TrackId

final case class Workspace(
  owner: UserId,
  selectedTrack: TrackId,
) {
  def selectTrack(track: Track): Workspace = copy(selectedTrack = track.id)
}

object Workspace {

  final case class WorkspaceId(id: Long) extends AnyVal

}