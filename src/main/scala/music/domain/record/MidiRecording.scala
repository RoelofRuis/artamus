package music.domain.record

final case class MidiRecording(
  resolution: Long,
  notes: Seq[RawMidiNote]
)
