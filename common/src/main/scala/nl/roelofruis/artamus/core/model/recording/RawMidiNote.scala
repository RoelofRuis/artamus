package nl.roelofruis.artamus.core.model.recording

import nl.roelofruis.artamus.core.model.primitives.{Loudness, MidiNoteNumber}

final case class RawMidiNote(
  noteNumber: MidiNoteNumber,
  loudness: Loudness,
  starts: MillisecondPosition,
)
