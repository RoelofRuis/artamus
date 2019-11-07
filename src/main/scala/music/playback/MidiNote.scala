package music.playback

import music.primitives.{MidiNoteNumber, Loudness, Window}

final case class MidiNote(
  noteNumber: MidiNoteNumber,
  window: Window,
  loudness: Loudness
)
