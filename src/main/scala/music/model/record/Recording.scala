package music.model.record

import java.util.UUID

import music.model.record.Recording.RecordingId

final case class Recording(
  id: RecordingId = RecordingId(),
  mode: RecordingMode = InputOnly,
  notes: Seq[RawMidiNote] = Seq()
) {
  def recordNote(note: RawMidiNote): Recording = copy(notes = notes :+ note)
}

object Recording {

  final case class RecordingId(id: UUID = UUID.randomUUID())

}