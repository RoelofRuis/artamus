package domain.display.layout

import domain.display.glyph.Glyphs.{Glyph, GlyphDuration, SingleGlyph}
import domain.display.layout.MetrePositioning.PositionedMetre
import domain.math.Rational
import domain.math.temporal.{Position, Window}

import scala.annotation.tailrec

object ElementLayout {

  final case class Element[A](window: Window, glyph: A)

  import domain.math.IntegerMath

  def layoutElements[A](
    elements: Seq[Element[A]],
    metres: LazyList[PositionedMetre],
    restGlyph: A,
  ): Seq[Glyph[A]] = {
    @tailrec
    def loop(state: LayoutState[A]): List[Glyph[A]] = {
      state.activeElement match {
        case None => // insert final rest
          state
            .withGlyphs(fitGlyphs(state.activeMetre, state.restUntilEndOfBar))
            .glyphs

        case Some((element, elementWindow)) =>
          if (elementWindow.start >= state.endOfActiveMeter) { // insert rest as long as bar, move to next bar
            loop(
              state
                .withGlyphs(fitGlyphs(state.activeMetre, state.restUntilEndOfBar))
                .to(state.endOfActiveMeter)
            )
          }
          else if (elementWindow.end <= state.endOfActiveMeter) {
            if (elementWindow.start > state.position) { // insert rest and continue with this bar
              loop(
                state
                  .withGlyphs(fitGlyphs(state.activeMetre, state.restUntil(elementWindow.start)))
                  .to(elementWindow.start)
              )
            }
            else { // insert element and if last element move to next bar
              loop(
                state
                  .withGlyphs(fitGlyphs(state.activeMetre, element))
                  .to(elementWindow.end)
                  .toNextElement
              )
            }
          }
          else { // insert element as long as bar, move to next bar
            loop(
              state
                .withGlyphs(fitGlyphs(state.activeMetre, element, tie=true))
                .to(state.endOfActiveMeter)
            )
          }
      }
    }

    // TODO: this can now be improved based on info given in metre!
    def fitGlyphs(metre: PositionedMetre, element: Element[A], tie: Boolean = false): Seq[Glyph[A]] = {
      metre.window.intersectNonInstant(element.window) match {
        case None => Seq.empty
        case Some(window) =>
          window.duration.v match {
            case r @ Rational(_, d) if ! d.isPowerOfTwo =>
              throw new NotImplementedError(s"Cannot fit tuplet [$r]!")

            case Rational(1, d) =>
              Seq(SingleGlyph(element.glyph, GlyphDuration(d.largestPowerOfTwo, 0, tie)))

            case Rational(3, d) =>
              Seq(SingleGlyph(element.glyph, GlyphDuration(d.largestPowerOfTwo - 1, 1, tie)))

            case Rational(5, d) =>
              Seq(
                SingleGlyph(element.glyph, GlyphDuration(d.largestPowerOfTwo - 2, 0, true)),
                SingleGlyph(element.glyph, GlyphDuration(d.largestPowerOfTwo, 0))
              )

            case Rational(7, d) =>
              Seq(SingleGlyph(element.glyph, GlyphDuration(d.largestPowerOfTwo - 2, 2)))

            case r => throw new NotImplementedError(s"Cannot fit length [$r]")
          }
      }
    }

    loop(LayoutState(Position.ZERO, elements, metres, restGlyph, List()))
  }

  private case class LayoutState[A](
    position: Position,
    private val elements: Seq[Element[A]],
    private val metres: LazyList[PositionedMetre],
    private val restGlyph: A,
    glyphs: List[Glyph[A]]
  ) {

    // Derived properties
    val activeMetre: PositionedMetre = metres.head
    val activeElement: Option[(Element[A], Window)] = elements.headOption.map(e => (e, e.window))

    // Helpers
    def endOfActiveMeter: Position = activeMetre.window.end

    def restUntilEndOfBar: Element[A] = Element(Window(position, activeMetre.window.end - position), restGlyph)

    def restUntil(until: Position): Element[A] = Element(Window(position, until - position), restGlyph)

    // State updates
    def withGlyphs(newGlyphs: Seq[Glyph[A]]): LayoutState[A] = copy(glyphs = glyphs ++ newGlyphs)

    def to(position: Position): LayoutState[A] = copy(
      position = position,
      metres = if (position >= endOfActiveMeter) metres.tail else metres
    )

    def toNextElement: LayoutState[A] = copy(elements = elements.tail)

  }

}

