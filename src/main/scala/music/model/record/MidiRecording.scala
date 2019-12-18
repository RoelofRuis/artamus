package music.model.record

final case class MidiRecording(
  resolution: Long,
  notes: Seq[RawMidiNote]
)
