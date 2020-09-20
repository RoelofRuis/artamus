package artamus.core.model.display.glyph

import artamus.core.model.primitives.{Function, PitchSpelling}

object ChordStaffGlyphFamily {

  sealed trait ChordStaffGlyph

  final case class ChordNameGlyph(
    root: PitchSpelling,
    functions: Set[Function]
  ) extends ChordStaffGlyph

  final case class ChordRestGlyph() extends ChordStaffGlyph

}
