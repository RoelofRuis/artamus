package nl.roelofruis.artamus.core.model.display.staff

import nl.roelofruis.artamus.core.model.display.glyph.ChordStaffGlyphFamily.ChordStaffGlyph
import nl.roelofruis.artamus.core.model.display.glyph.Glyphs.Glyph

final case class ChordStaff(
  glyphs: Seq[Glyph[ChordStaffGlyph]]
) extends Staff
