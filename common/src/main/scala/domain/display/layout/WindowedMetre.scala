package domain.display.layout

import domain.math.temporal.{Position, Window}
import domain.primitives.Metre

final case class WindowedMetre(
  position: Position,
  metre: Metre
) {
  lazy val window: Window = Window(position, metre.duration)
}
