package music.domain.record

import music.math.temporal.Position
import music.primitives.{Loudness, MidiNoteNumber}

final case class RawMidiNote(
  noteNumber: MidiNoteNumber,
  loudness: Loudness,
  starts: Position,
)
