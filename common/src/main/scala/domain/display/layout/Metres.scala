package domain.display.layout

import domain.math.temporal.{Position, Window}
import domain.primitives.TimeSignature

// TODO: make work with actual time signatures instead of fixed value
final case class Metres() {

  def iterateMetres: LazyList[Metre] = {
    val activeTimeSignature = TimeSignature.`4/4`

    def loop(searchPos: Position): LazyList[Metre] = {
      val metreWindow = Window(searchPos, activeTimeSignature.division.barDuration)
      Metre(metreWindow) #:: loop(searchPos + metreWindow.duration)
    }

    loop(Position.ZERO)
  }

}

object Metres {

  @deprecated
  final case class Bar(
    barWindow: Window,
  )

}