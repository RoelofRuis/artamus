package domain.display.layout

import domain.math.temporal.Window

final case class Metre(
  window: Window,
) {
  def subdivisions: Seq[Metre] = ???
}

