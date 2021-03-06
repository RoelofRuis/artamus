package artamus.core.model.display.glyph

import artamus.core.math.FractionalPowerOfTwo

object Glyphs {

  sealed trait Glyph[A]

  final case class GlyphDuration(n: FractionalPowerOfTwo, dots: Int, tieToNext: Boolean = false)

  final case class InstantGlyph[A](glyph: A) extends Glyph[A]

  final case class SingleGlyph[A](glyph: A, properties: GlyphDuration) extends Glyph[A]

  final case class TupletGlyph[A](contents: Seq[Glyph[A]], factor: Int) extends Glyph[A]

}
