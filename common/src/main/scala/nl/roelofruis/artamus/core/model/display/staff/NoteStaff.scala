package nl.roelofruis.artamus.core.model.display.staff

import nl.roelofruis.artamus.core.model.display.glyph.StaffGlyphFamily.StaffGlyph
import nl.roelofruis.artamus.core.model.display.staff.NoteStaff.Clef
import nl.roelofruis.artamus.core.model.display.glyph.Glyphs.Glyph

final case class NoteStaff(
  clef: Clef,
  glyphs: Seq[Glyph[StaffGlyph]]
) extends Staff

object NoteStaff {

  sealed trait Clef
  case object Treble extends Clef
  case object Bass extends Clef

}