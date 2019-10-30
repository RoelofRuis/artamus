package music.analysis

import music.math.Rational
import music.primitives.{Duration, Position, TimeSignatureDivision, Window}
import music.symbol.TimeSignature
import music.symbol.collection.SymbolView

import scala.annotation.tailrec

object BarAnalysis {

  implicit class BarOps(view: SymbolView[TimeSignature]) {

    type BarNumber = Int

    // TODO: make this dynamic and use all given time signatures
    val ts: TimeSignature = view
      .firstAt(Position.zero)
      .map(_.symbol)
      .getOrElse(TimeSignature(TimeSignatureDivision.`4/4`))

    def fitToBars(window: Window): Seq[Window] = {
      val startBar = getBarForPosition(window.start)
      val endBar = getBarForPosition(window.end)

      if (startBar == endBar) Seq(window)
      else Range.inclusive(startBar, endBar)
        .map(getBarWindow)
        .flatMap(window.intersect)
    }

    private def getBarForPosition(pos: Position): BarNumber = durationFits(pos, ts.division.barDuration, 0)

    private def getBarWindow(bar: BarNumber): Window = Window(
      Position(ts.division.barDuration.value * bar),
      Position(ts.division.barDuration.value * (bar + 1))
    )

    @tailrec
    private def durationFits(pos: Position, dur: Duration, acc: Int): Int = {
      val newPos = pos - dur
      if (newPos.value <= Rational(0)) acc
      else durationFits(newPos, dur, acc + 1)
    }

  }

}
