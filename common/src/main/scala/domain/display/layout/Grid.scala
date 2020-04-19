package domain.display.layout

import domain.math.temporal.{Position, Window}
import domain.primitives.TimeSignature

import scala.annotation.tailrec

class Grid() {

  def getBars(window: Window): List[Bar] = {

    val from = window.start
    val until = window.end

    val activeTimeSignature = TimeSignature.`4/4` // TODO: actually get and update this while searching

    @tailrec
    def nextBar(searchPos: Position, result: List[Bar]): List[Bar] = {
      val barWindow = Window(searchPos, activeTimeSignature.division.barDuration)
      if (barWindow.end <= from) nextBar(searchPos + barWindow.duration, result)
      else if (barWindow.start > until) result
      else nextBar(searchPos + barWindow.duration, result :+ Bar(barWindow))
    }

    nextBar(Position.ZERO, List())
  }

}
