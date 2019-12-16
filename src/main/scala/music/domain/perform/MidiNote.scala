package music.domain.perform

import music.math.temporal.Window
import music.domain.primitives.{Loudness, MidiNoteNumber}

final case class MidiNote(
  noteNumber: MidiNoteNumber,
  window: Window,
  loudness: Loudness
)
