package domain.display.layout

import domain.display.glyph.Glyphs.{Glyph, SingleGlyph, TupletGlyph}
import domain.display.layout.BarLayout.{BarElements, PlaceholderRest}
import domain.display.layout.Bars.Bar

import scala.annotation.tailrec

object LayerLayout {

  def layoutGlyphs[A](
    elements: Seq[Windowed[A]],
    restGlyph: A,
    bars: Bars
  ): Seq[Glyph[A]] = {

    // TODO: case met 0 elements moet 1 lege bar renderen!
    calculateBarElements(elements, bars.iterateBars)
      .flatMap(barElements => resolveRests(barElements.glyphSequence, restGlyph))
  }

  private def calculateBarElements[A](windowed: Seq[Windowed[A]], bars: LazyList[Bar]): List[BarElements[A]] = {
    def addToBar(acc: List[BarElements[A]], currentBar: Bar, currentWindowed: Windowed[A]): List[BarElements[A]] = {
      acc.headOption match {
        // no bars -> insert windowed in first bar
        case None => BarElements(currentBar, Seq(currentWindowed)) +: acc

        // previous bar -> insert windowed in next bar
        case Some(barElements) if barElements.bar != currentBar => BarElements(currentBar, Seq(currentWindowed)) +: acc

        // current bar -> add windowed to this bar
        case Some(barElements) => barElements.copy(elements = barElements.elements :+ currentWindowed) +: acc.tail
      }
    }

    @tailrec
    def fillBarElements(acc: List[BarElements[A]], windowed: Seq[Windowed[A]], bars: LazyList[Bar]): List[BarElements[A]] = {
      windowed.headOption match {
        case None => acc
        case Some(currentWindowed) =>
          val currentBar = bars.head
          val currentWindow = currentWindowed.window

          if (currentWindow.start >= currentBar.barWindow.end) { // starts after this bar -> insert empty bar
            fillBarElements(BarElements[A](currentBar, Seq()) +: acc, windowed, bars.tail)
          }
          else if (currentWindow.end <= currentBar.barWindow.end) { // fits in this bar -> insert windowed and see if more fits
            fillBarElements(addToBar(acc, currentBar, currentWindowed), windowed.tail, bars)
          }
          else { // partially fits in this bar -> insert windowed and move to next bar
            fillBarElements(addToBar(acc, currentBar, currentWindowed), windowed, bars.tail)
          }
      }
    }
    fillBarElements(List(), windowed, bars).reverse
  }

  private def resolveRests[A](glyphs: Seq[Glyph[Either[PlaceholderRest.type, A]]], restGlyph: A): Seq[Glyph[A]] = {
    glyphs.map {
      case SingleGlyph(Left(PlaceholderRest), p) => SingleGlyph(restGlyph, p)
      case SingleGlyph(Right(a), p) => SingleGlyph(a, p)
      case TupletGlyph(contents, factor) => TupletGlyph(resolveRests(contents, restGlyph), factor)
    }
  }

}

