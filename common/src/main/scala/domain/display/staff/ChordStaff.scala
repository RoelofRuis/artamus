package domain.display.staff

import domain.display.glyph.ChordStaffGlyphFamily.ChordStaffGlyph
import domain.display.glyph.Glyphs.Glyph

final case class ChordStaff(
  glyphs: Seq[Glyph[ChordStaffGlyph]]
) extends Staff
