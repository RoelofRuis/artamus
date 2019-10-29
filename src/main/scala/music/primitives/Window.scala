package music.primitives

final case class Window(start: Position, end: Position) {

  def durationUntil(that: Window): Duration = that.start - end

}

object Window {

  def apply(start: Position, duration: Duration): Window = Window(start, Position(start.value + duration.value))

}