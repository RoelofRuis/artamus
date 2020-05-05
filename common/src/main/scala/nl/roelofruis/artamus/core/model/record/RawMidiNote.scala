package nl.roelofruis.artamus.core.model.record

import domain.primitives.{Loudness, MidiNoteNumber}

final case class RawMidiNote(
  noteNumber: MidiNoteNumber,
  loudness: Loudness,
  starts: MillisecondPosition,
)
