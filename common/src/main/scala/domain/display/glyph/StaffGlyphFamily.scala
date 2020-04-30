package domain.display.glyph

import domain.math.FractionalPowerOfTwo
import domain.primitives._

object StaffGlyphFamily {

  sealed trait StaffGlyph

  final case class NoteGroupGlyph(
    notes: Seq[ScientificPitch],
  ) extends StaffGlyph {
    def isEmpty: Boolean = notes.isEmpty
    def isChord: Boolean = notes.size > 1
  }

  final case class RestGlyph() extends StaffGlyph

  final case class KeyGlyph(
    root: PitchSpelling,
    scale: Scale
  ) extends StaffGlyph

  final case class TimeSignatureGlyph(
    num: Int,
    denom: FractionalPowerOfTwo
  ) extends StaffGlyph

}
