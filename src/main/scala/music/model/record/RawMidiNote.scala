package music.model.record

import music.primitives.{Loudness, MidiNoteNumber, TickPosition}

final case class RawMidiNote(
  noteNumber: MidiNoteNumber,
  loudness: Loudness,
  starts: TickPosition,
)
