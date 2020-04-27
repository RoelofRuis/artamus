package domain.display.layout

import domain.display.layout.Bars.Bar
import domain.math.temporal.{Position, Window}
import domain.primitives.TimeSignature

// TODO: make work with actual time signatures instead of fixed value
final case class Bars() {

  def iterateBars: LazyList[Bar] = {
    val activeTimeSignature = TimeSignature.`4/4`

    def nextBar(searchPos: Position): LazyList[Bar] = {
      val barWindow = Window(searchPos, activeTimeSignature.division.barDuration)
      Bar(barWindow) #:: nextBar(searchPos + barWindow.duration)
    }

    nextBar(Position.ZERO)
  }

}

object Bars {

  final case class Bar(
    barWindow: Window,
  )

}