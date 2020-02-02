package domain.display.staff

import domain.display.Staff

final case class NoteStaff(
  clef: Clef,
  glyphs: Iterator[StaffGlyph]
) extends Staff
