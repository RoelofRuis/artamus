package music.model.display.staff

import music.model.display.Staff

final case class NoteStaff(
  clef: Clef,
  glyphs: Iterator[StaffGlyph]
) extends Staff
