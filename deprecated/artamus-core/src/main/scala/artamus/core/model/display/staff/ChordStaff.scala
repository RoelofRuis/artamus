package artamus.core.model.display.staff

import artamus.core.model.display.glyph.ChordStaffGlyphFamily.ChordStaffGlyph
import artamus.core.model.display.glyph.Glyphs.Glyph

final case class ChordStaff(
  glyphs: Seq[Glyph[ChordStaffGlyph]]
) extends Staff
