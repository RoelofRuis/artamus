package music.model.perform

import math.temporal.Window
import music.primitives.{Loudness, MidiNoteNumber}

final case class MidiNote(
  noteNumber: MidiNoteNumber,
  window: Window,
  loudness: Loudness
)
