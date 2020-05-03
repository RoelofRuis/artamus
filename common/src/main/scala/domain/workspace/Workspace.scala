package domain.workspace

import domain.math.Rational
import domain.math.temporal.Duration
import domain.record.transfer.{Quantizer, RecordTransfer}
import domain.workspace.User.UserId
import domain.write.Track
import domain.write.Track.TrackId

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