package nl.roelofruis.artamus.core.layout.algorithms

import nl.roelofruis.artamus.core.common.Position
import nl.roelofruis.artamus.core.common.Temporal.{Windowed, Timeline}
import nl.roelofruis.artamus.core.layout.Glyph
import nl.roelofruis.artamus.core.layout.Glyph.SingleGlyph
import nl.roelofruis.artamus.core.track.Temporal.Metre

import scala.annotation.tailrec

object Layout {

  def layoutElements[A](
    elements: Timeline[A],
    layout: LayoutDescription[A]
  ): Seq[Glyph[A]] = {
    @tailrec
    def loop(state: LayoutState[A]): List[Glyph[A]] = {
      state.activeElement match {
        case None =>
          if (state.position == state.activeMetre.window.start) { // ended exactly on final bar
            state.glyphs
          }
          else { // insert final rest
            state
              .withGlyphs(fitElement(state.activeMetre, state.restUntilEndOfBar))
              .glyphs
          }

        case Some(element) =>
          val instantGlyphs = layout.instantGlyphs(state.position)

          if (element.window.start >= state.endOfActiveMeter) { // insert rest as long as bar, move to next bar
            loop(
              state
                .withGlyphs(instantGlyphs)
                .withGlyphs(fitElement(state.activeMetre, state.restUntilEndOfBar))
                .to(state.endOfActiveMeter)
            )
          }
          else if (element.window.end <= state.endOfActiveMeter) {
            if (element.window.start > state.position) { // insert rest and continue with this bar
              loop(
                state
                  .withGlyphs(instantGlyphs)
                  .withGlyphs(fitElement(state.activeMetre, state.restUntil(element.window.start)))
                  .to(element.window.start)
              )
            }
            else { // insert element and if last element move to next bar
              loop(
                state
                  .withGlyphs(instantGlyphs)
                  .withGlyphs(fitElement(state.activeMetre, element))
                  .to(element.window.end)
                  .toNextElement
              )
            }
          }
          else { // insert element as long as bar, move to next bar
            loop(
              state
                .withGlyphs(instantGlyphs)
                .withGlyphs(fitElement(state.activeMetre, element, tie=true))
                .to(state.endOfActiveMeter)
            )
          }
      }
    }

    def fitElement(metre: Windowed[Metre], element: Windowed[A], tie: Boolean = false): Seq[Glyph[A]] = {
      GlyphFitting.fitDurations(metre, element.window, tie).map { dur => SingleGlyph(element.get, dur) }
    }

    loop(LayoutState(Position.ZERO, elements, layout.metres, layout.restGlyph, List()))
  }

  private case class LayoutState[A](
    position: Position,
    private val elements: Timeline[A],
    private val metres: LazyList[Windowed[Metre]],
    private val restGlyph: A,
    glyphs: List[Glyph[A]]
  ) {

    // Derived properties
    val activeMetre: Windowed[Metre] = metres.head
    val activeElement: Option[Windowed[A]] = elements.headOption

    // Helpers
    def endOfActiveMeter: Position = activeMetre.window.end

    def restUntilEndOfBar: Windowed[A] = Windowed(position, activeMetre.window.end - position, restGlyph)

    def restUntil(until: Position): Windowed[A] = Windowed(position, until - position, restGlyph)

    // State updates
    def withGlyphs(newGlyphs: Seq[Glyph[A]]): LayoutState[A] = copy(glyphs = glyphs ++ newGlyphs)

    def to(position: Position): LayoutState[A] = copy(
      position = position,
      metres = if (position >= endOfActiveMeter) metres.tail else metres
    )

    def toNextElement: LayoutState[A] = copy(elements = elements.tail)

  }

}

