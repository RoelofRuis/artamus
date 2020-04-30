package domain.display.layout

import domain.display.glyph.Glyphs.{Glyph, GlyphDuration, SingleGlyph}
import domain.display.layout.MetrePositioning.PositionedMetre
import domain.math.Rational
import domain.math.temporal.{Position, Window}

import scala.annotation.tailrec

object ElementLayout {

  final case class Element[A](window: Window, glyph: A)

  import domain.math.IntegerMath

  private case class LayoutState[A](
    position: Position,
    elements: Seq[Element[A]],
    metres: LazyList[PositionedMetre],
    glyphs: List[Glyph[A]] = List()
  ) {

    val activeMetre: PositionedMetre = metres.head

    val activeElement: Option[(Element[A], Window)] = elements.headOption.map(e => (e, e.window))

    def windowOfActiveMeter: Window = activeMetre.window

    def endOfActiveMeter: Position = windowOfActiveMeter.end

    def windowUntilEndOfBar: Window = Window(position, activeMetre.window.end - position)

    def withGlyphs(newGlyphs: Seq[Glyph[A]]): LayoutState[A] = copy(glyphs = glyphs ++ newGlyphs)

    def to(position: Position): LayoutState[A] = {
      copy(
        position = position,
        metres = if (position >= endOfActiveMeter) metres.tail else metres
      )
    }

    def toNextElement: LayoutState[A] = copy(elements = elements.tail)
  }

  def layoutElements[A](
    elements: Seq[Element[A]],
    metres: LazyList[PositionedMetre],
    restGlyph: A,
  ): Seq[Glyph[A]] = {
    def restElement(window: Window): Element[A] = Element(window, restGlyph)

    @tailrec
    def loop(state: LayoutState[A]): List[Glyph[A]] = {
      state.activeElement match {
        case None => // insert final rest
          state
            .withGlyphs(fitGlyphs(state.activeMetre, restElement(state.windowUntilEndOfBar)))
            .glyphs

        case Some((element, elementWindow)) =>
          if (elementWindow.start >= state.endOfActiveMeter) { // insert rest as long as bar, move to next bar
            loop(
              state
                .withGlyphs(fitGlyphs(state.activeMetre, restElement(state.windowOfActiveMeter)))
                .to(state.endOfActiveMeter)
            )
          }
          else if (elementWindow.end <= state.endOfActiveMeter) {
            if (elementWindow.start > state.position) { // insert rest and continue with this bar
              loop(
                state
                  .withGlyphs(fitGlyphs(state.activeMetre, restElement(Window(state.position, elementWindow.start - state.position))))
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


    loop(LayoutState(Position.ZERO, elements, metres))
  }


}

