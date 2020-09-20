package artamus.core.model.primitives

import artamus.core.math.temporal.Window

final case class NoteGroup(
  window: Window,
  notes: Seq[Note] // TODO: maybe make this set?
)
