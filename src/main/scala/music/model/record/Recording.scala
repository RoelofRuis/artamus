package music.model.record

import java.util.UUID

import music.model.record.Recording.RecordingId
import music.primitives.TickResolution

final case class Recording(
  resolution: TickResolution,
  id: RecordingId = RecordingId(),
  mode: RecordingMode = InputOnly,
  notes: Seq[RawMidiNote] = Seq()
)

object Recording {

  final case class RecordingId(id: UUID = UUID.randomUUID())

}