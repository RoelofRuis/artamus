package music.model.record

import music.primitives.{Loudness, MidiNoteNumber, MillisecondPosition}

final case class RawMidiNote(
  noteNumber: MidiNoteNumber,
  loudness: Loudness,
  starts: MillisecondPosition,
)
