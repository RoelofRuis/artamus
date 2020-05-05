package nl.roelofruis.artamus.core.model.performance

import nl.roelofruis.math.temporal.Window
import nl.roelofruis.artamus.core.model.primitives.{Loudness, MidiNoteNumber}

final case class MidiNote(
  noteNumber: MidiNoteNumber,
  window: Window,
  loudness: Loudness
)
