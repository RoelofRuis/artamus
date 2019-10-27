package music.primitives

final case class Window(start: Position, end: Position) {

  def durationUntil(that: Window): Duration = that.start - end

}
