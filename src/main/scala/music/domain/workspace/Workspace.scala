package music.domain.workspace

import music.domain.track.Track.TrackId
import music.domain.user.User.UserId

final case class Workspace(
  owner: UserId,
  editedTrack: TrackId
)

object Workspace {

  final case class WorkspaceId(id: Long) extends AnyVal

}