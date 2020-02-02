package domain.record

import domain.primitives.{Loudness, MidiNoteNumber}

final case class RawMidiNote(
  noteNumber: MidiNoteNumber,
  loudness: Loudness,
  starts: MillisecondPosition,
)
