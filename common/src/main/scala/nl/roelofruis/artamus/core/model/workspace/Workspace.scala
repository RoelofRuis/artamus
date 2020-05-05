package nl.roelofruis.artamus.core.model.workspace

import nl.roelofruis.math.Rational
import nl.roelofruis.math.temporal.Duration
import nl.roelofruis.artamus.core.ops.formalise.{Quantizer, RecordTransfer}
import nl.roelofruis.artamus.core.model.workspace.User.UserId
import nl.roelofruis.artamus.core.model.track.Track
import nl.roelofruis.artamus.core.model.track.Track.TrackId

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