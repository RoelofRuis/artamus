package artamus.core.model.workspace

import artamus.core.math.Rational
import artamus.core.math.temporal.Duration
import artamus.core.ops.formalise.{Quantizer, RecordTransfer}
import artamus.core.model.workspace.User.UserId
import artamus.core.model.track.Track
import artamus.core.model.track.Track.TrackId

final case class Workspace(
  owner: UserId,
  editingTrack: TrackId,
  recordTransfer: RecordTransfer = RecordTransfer(Quantizer(), rhythmOnly=false, Duration(Rational(1, 4)))
) {

  def editTrack(track: Track): Workspace = {
    copy(editingTrack = track.id)
  }
}

object Workspace {

  final case class WorkspaceId(id: Long) extends AnyVal

}