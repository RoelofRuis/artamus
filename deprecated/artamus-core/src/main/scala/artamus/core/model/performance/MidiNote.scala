package artamus.core.model.performance

import artamus.core.math.temporal.Window
import artamus.core.model.primitives.{Loudness, MidiNoteNumber}

final case class MidiNote(
  noteNumber: MidiNoteNumber,
  window: Window,
  loudness: Loudness
)
