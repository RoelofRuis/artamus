package nl.roelofruis.artamus.core.model.perform

import domain.math.temporal.Window
import domain.primitives.{Loudness, MidiNoteNumber}

final case class MidiNote(
  noteNumber: MidiNoteNumber,
  window: Window,
  loudness: Loudness
)
