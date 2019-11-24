package music.domain.track

import music.math.temporal.{Duration, Position, Window}
import music.primitives.TimeSignatureDivision

import scala.annotation.tailrec
import scala.collection.immutable.SortedMap

// TODO: extract interface
// TODO: see whether this integrates as SymbolTrack[TimeSignature] or some related implementation
final case class Bars private (
  timeSignatures: SortedMap[Position, TimeSignature]
) {

  // TODO: this should be dynamic, and initial should be determined from timeSignatures map
  private val ts = timeSignatures.head._2
  def initialTimeSignature: TimeSignature = ts

  type BarNumber = Int

  def writeTimeSignature(pos: Position, ts: TimeSignature): Bars = new Bars(timeSignatures.updated(pos, ts))

  def fillBarFrom(window: Window): Window = {
    val bar = durationFits(window.end, ts.division.barDuration, inclusive=true)
    Window(window.end, getBarWindow(bar).end - window.end)
  }

  def fit(window: Window): Seq[Window] = {
    // if a note starts on bar start, do not include in previous bar TODO: make the code explain this better
    val startBar = durationFits(window.start, ts.division.barDuration, inclusive=false)
    val endBar = durationFits(window.end, ts.division.barDuration, inclusive=true)

    if (startBar == endBar) Seq(window)
    else Range.inclusive(startBar, endBar)
      .map(getBarWindow)
      .flatMap(window.intersect)
  }

  private def getBarWindow(bar: BarNumber): Window = Window(
    Position.at(ts.division.barDuration * bar),
    ts.division.barDuration
  )

  private def durationFits(pos: Position, dur: Duration, inclusive: Boolean): Int = {
    @tailrec
    def loopExclusive(pos: Position, dur: Duration, acc: Int): Int = {
      val newPos = pos - dur
      if (newPos < Position.ZERO) acc
      else loopExclusive(newPos, dur, acc + 1)
    }
    @tailrec
    def loopInclusive(pos: Position, dur: Duration, acc: Int): Int = {
      val newPos = pos - dur
      if (newPos <= Position.ZERO) acc
      else loopInclusive(newPos, dur, acc + 1)
    }
    if (inclusive) loopInclusive(pos, dur, acc = 0) else loopExclusive(pos, dur, acc = 0)
  }

}

object Bars {

  def apply(): Bars = {
    new Bars(SortedMap(Position.ZERO -> TimeSignature(TimeSignatureDivision.`4/4`)))
  }

}
