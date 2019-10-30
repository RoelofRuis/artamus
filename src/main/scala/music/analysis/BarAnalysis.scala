package music.analysis

import music.primitives.{Duration, Position, TimeSignatureDivision, Window}
import music.symbol.TimeSignature
import music.symbol.collection.{SymbolView, Track}

import scala.annotation.tailrec

object BarAnalysis {

  implicit class BarOps(view: SymbolView[TimeSignature]) {

    // TODO: make this dynamic and use all given time signatures
    val ts: TimeSignature = view
      .firstAt(Position.zero)
      .map(_.symbol)
      .getOrElse(TimeSignature(TimeSignatureDivision.`4/4`))

    def getBarForPosition(pos: Position): Int = durationFits(pos, ts.division.barDuration, 0)

    def getBarWindow(bar: Int): Window = Window(
      Position(ts.division.barDuration.value * bar),
      Position(ts.division.barDuration.value * (bar + 1))
    )

    @tailrec
    private def durationFits(pos: Position, dur: Duration, acc: Int): Int = {
      val newPos = pos - dur
      if (newPos.isNegative) acc
      else durationFits(newPos, dur, acc + 1)
    }

  }

}
