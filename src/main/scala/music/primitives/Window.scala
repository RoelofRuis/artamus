package music.primitives

final case class Window(start: Position, end: Position) {

  def diff(that: Window): Duration = end - that.start

}

object Window {

  lazy val zero: Window = Window(Position.zero, Position.zero)

}