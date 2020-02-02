package domain.primitives

import domain.math.temporal.Window

final case class NoteGroup(
  window: Window,
  notes: Seq[Note]
)
