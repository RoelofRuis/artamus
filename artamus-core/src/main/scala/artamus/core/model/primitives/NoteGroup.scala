package artamus.core.model.primitives

import nl.roelofruis.math.temporal.Window

final case class NoteGroup(
  window: Window,
  notes: Seq[Note] // TODO: maybe make this set?
)
