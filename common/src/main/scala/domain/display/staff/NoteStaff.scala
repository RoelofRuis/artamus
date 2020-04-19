package domain.display.staff

import domain.display.glyph.StaffGlyphFamily.StaffGlyph
import domain.display.staff.NoteStaff.Clef
import domain.display.glyph.Glyphs.Glyph

final case class NoteStaff(
  clef: Clef,
  glyphs: Seq[Glyph[StaffGlyph]]
) extends Staff

object NoteStaff {

  sealed trait Clef
  case object Treble extends Clef
  case object Bass extends Clef

}