package music.model.record

import music.primitives.TickResolution

final case class MidiRecording(
  resolution: TickResolution,
  notes: Seq[RawMidiNote]
)
