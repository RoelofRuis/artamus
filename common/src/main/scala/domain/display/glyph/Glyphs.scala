package domain.display.glyph

object Glyphs {

  sealed trait Glyph[A]

  /**
   * @param n The base duration. Expressed as the `n` value in `1 / pow(2, n)`
   * @param dots The number of dots, each dots adds half of the previous note value to the duration.
   * @param tieToNext Whether this glyph represents a partial duration and might be tied to the next glyph
   */
  final case class GlyphDuration(n: Int, dots: Int, tieToNext: Boolean = false)

  final case class SingleGlyph[A](glyph: A, properties: GlyphDuration) extends Glyph[A]

  final case class TupletGlyph[A](contents: List[Glyph[A]], factor: Int) extends Glyph[A]

}
