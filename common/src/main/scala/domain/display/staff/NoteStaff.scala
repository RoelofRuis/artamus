package domain.display.staff

import domain.display.Staff

final case class NoteStaff(
  clef: Clef,
  glyphs: Seq[StaffGlyph]
) extends Staff
