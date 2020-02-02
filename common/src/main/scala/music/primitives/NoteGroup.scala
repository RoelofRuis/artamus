package music.primitives

import math.temporal.Window

final case class NoteGroup(
  window: Window,
  notes: Seq[Note]
)
