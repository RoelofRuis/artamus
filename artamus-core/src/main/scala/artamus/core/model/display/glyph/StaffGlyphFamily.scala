package artamus.core.model.display.glyph

import artamus.core.model.display.glyph.Glyphs.{Glyph, InstantGlyph}
import nl.roelofruis.math.FractionalPowerOfTwo
import nl.roelofruis.math.temporal.Position
import artamus.core.model.primitives._

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

  def keyBuilder(keys: Map[Position, Key]): Position => Option[Glyph[StaffGlyph]] = position => {
    keys
      .get(position)
      .map { key =>
        InstantGlyph(KeyGlyph(key.root, key.scale))
      }
  }

  def timeSignatureBuilder(metres: Map[Position, Metre]): Position => Option[Glyph[StaffGlyph]] = position => {
    import nl.roelofruis.math._
    metres
      .get(position)
      .map { metre =>
        val (num, denom) = metre.getTimeSignatureFraction
        InstantGlyph(TimeSignatureGlyph(num, 2**denom))
      }
  }

}
