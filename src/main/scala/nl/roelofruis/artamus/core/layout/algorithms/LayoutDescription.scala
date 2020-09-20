package nl.roelofruis.artamus.core.layout.algorithms

import nl.roelofruis.artamus.core.common.Containers.Positioned
import nl.roelofruis.artamus.core.common.Position
import nl.roelofruis.artamus.core.layout.Glyph
import nl.roelofruis.artamus.core.track.Temporal.Metre

final case class LayoutDescription[A](
  metres: LazyList[Positioned[Metre]],
  restGlyph: A,
  instantGlyphs: Position => Seq[Glyph[A]] = (_: Position) => Seq.empty
)

object LayoutDescription {

  def apply[A](
    metres: LazyList[Positioned[Metre]],
    restGlyph: A,
    instantGlyphBuilders: Seq[Position => Option[Glyph[A]]]
  ): LayoutDescription[A] = {
    LayoutDescription(metres, restGlyph, pos => instantGlyphBuilders.flatMap { _(pos) })
  }

}