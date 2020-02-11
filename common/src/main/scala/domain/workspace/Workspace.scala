package domain.workspace

import domain.workspace.User.UserId
import domain.write.Track
import domain.write.Track.TrackId

final case class Workspace(
  owner: UserId,
  editingTrack: TrackId,
  proposedTrack: Option[TrackId] = None // TODO: allow multiple proposals at the same time
) {

  def proposeTrack(track: Track): Workspace = copy(proposedTrack = Some(track.id))

  def editTrack(track: Track): Workspace = copy(editingTrack = track.id)
}

object Workspace {

  final case class WorkspaceId(id: Long) extends AnyVal

}