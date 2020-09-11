package nl.roelofruis.artamus.core.layout

import nl.roelofruis.artamus.core.Maths.FractionalPowerOfTwo

sealed trait Glyph[A]

object Glyph {

  final case class GlyphDuration(n: FractionalPowerOfTwo, dots: Int, tieToNext: Boolean = false)

  final case class InstantGlyph[A](glyph: A) extends Glyph[A]

  final case class SingleGlyph[A](glyph: A, properties: GlyphDuration) extends Glyph[A]

  final case class TupletGlyph[A](contents: Seq[Glyph[A]], factor: Int) extends Glyph[A]

}
