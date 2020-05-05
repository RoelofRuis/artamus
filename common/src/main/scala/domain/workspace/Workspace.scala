package domain.workspace

import domain.math.Rational
import domain.math.temporal.Duration
import nl.roelofruis.artamus.core.ops.formalise.{Quantizer, RecordTransfer}
import domain.workspace.User.UserId
import nl.roelofruis.artamus.core.model.write.Track
import nl.roelofruis.artamus.core.model.write.Track.TrackId

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