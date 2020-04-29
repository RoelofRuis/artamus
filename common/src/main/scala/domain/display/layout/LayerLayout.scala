package domain.display.layout

import domain.display.glyph.Glyphs.{Glyph, GlyphDuration, SingleGlyph}
import domain.math.Rational
import domain.math.temporal.{Position, Window}

import scala.annotation.tailrec

object LayerLayout {

  import domain.math.IntegerMath

  final case class Metre(
    window: Window,
  ) {
    def subdivisions: Seq[Metre] = ???
  }

  def layoutGlyphs[A](
    elements: Seq[Element[A]],
    restGlyph: A,
    metres: Metres
  ): Seq[Glyph[A]] = {
    def restElement(window: Window): Element[A] = Element(window, restGlyph)

    @tailrec
    def layoutElements(
      acc: List[Glyph[A]],
      position: Position,
      elements: Seq[Element[A]],
      metres: LazyList[Metre]
    ): List[Glyph[A]] = {
      val metre = metres.head

      elements.headOption match {
        case None => // insert final rest
          val newGlyphs = fitGlyphs(metre, restElement(Window(position, metre.window.end - position)))
          acc ++ newGlyphs

        case Some(element) =>
          val elementWindow = element.window

          if (elementWindow.start >= metre.window.end) { // insert rest as long as bar, move to next bar
            val newGlyphs = fitGlyphs(metre, restElement(metre.window))
            layoutElements(
              acc ++ newGlyphs,
              metre.window.end,
              elements,
              metres.tail
            )
          }
          else if (elementWindow.end <= metre.window.end) {
            if (elementWindow.start > position) { // insert rest and continue with this bar
              val newGlyphs = fitGlyphs(metre, restElement(Window(position, elementWindow.start - position)))
              layoutElements(
                acc ++ newGlyphs,
                elementWindow.start,
                elements,
                metres
              )
            }
            else { // insert element and if last element move to next bar
              val newGlyphs = fitGlyphs(metre, element)
              layoutElements(
                acc ++ newGlyphs,
                elementWindow.end,
                elements.tail,
                if (elementWindow.end == metre.window.end) metres.tail else metres
              )
            }
          }
          else { // insert element as long as bar, move to next bar
            val newGlyphs = fitGlyphs(metre, element, tie=true)
            layoutElements(
              acc ++ newGlyphs,
              metre.window.end,
              elements,
              metres.tail
            )
          }
      }
    }

    // TODO: this can now be improved based on info given in metre!
    def fitGlyphs(metre: Metre, element: Element[A], tie: Boolean = false): Seq[Glyph[A]] = {
      metre.window.intersectNonInstant(element.window) match {
        case None => Seq.empty
        case Some(window) =>
          window.duration.v match {
            case r @ Rational(_, d) if ! d.isPowerOfTwo => throw new NotImplementedError(s"Cannot fit tuplet [$r]!")
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


    layoutElements(List(), Position.ZERO, elements, metres.iterateMetres)
  }


}

