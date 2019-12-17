package music.domain.primitives

import music.math.temporal.Window

final case class NoteGroup(
  window: Window,
  notes: Seq[Note]
)