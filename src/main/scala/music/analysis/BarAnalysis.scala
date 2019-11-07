package music.analysis

import music.math.Rational
import music.primitives.{Duration, Position, TimeSignatureDivision, Window}
import music.symbol.TimeSignature
import music.symbol.collection.Track

import scala.annotation.tailrec

object BarAnalysis {

  implicit class BarOps(track: Track) {

    type BarNumber = Int

    // TODO: make this dynamic and use all given time signatures
    val ts: TimeSignature = track
      .read[TimeSignature]()
      .headOption
      .map(_.symbol)
      .getOrElse(TimeSignature(TimeSignatureDivision.`4/4`))

    def fillBarFrom(window: Window): Window = {
      val bar = durationFits(window.end, ts.division.barDuration, inclusive=true)
      Window(window.end, getBarWindow(bar).end - window.end)
    }

    def fitToBars(window: Window): Seq[Window] = {
      // if a note starts on bar start, do not include in previous bar TODO: make the code explain this better
      val startBar = durationFits(window.start, ts.division.barDuration, inclusive=false)
      val endBar = durationFits(window.end, ts.division.barDuration, inclusive=true)

      if (startBar == endBar) Seq(window)
      else Range.inclusive(startBar, endBar)
        .map(getBarWindow)
        .flatMap(window.intersect)
    }

    private def getBarWindow(bar: BarNumber): Window = Window(
      Position(ts.division.barDuration.value * bar),
      ts.division.barDuration
    )

    private def durationFits(pos: Position, dur: Duration, inclusive: Boolean): Int = {
      @tailrec
      def loopExclusive(pos: Position, dur: Duration, acc: Int): Int = {
        val newPos = pos - dur
        if (newPos.value < Rational(0)) acc
        else loopExclusive(newPos, dur, acc + 1)
      }
      @tailrec
      def loopInclusive(pos: Position, dur: Duration, acc: Int): Int = {
        val newPos = pos - dur
        if (newPos.value <= Rational(0)) acc
        else loopInclusive(newPos, dur, acc + 1)
      }
      if (inclusive) loopInclusive(pos, dur, acc = 0) else loopExclusive(pos, dur, acc = 0)
    }

  }

}
