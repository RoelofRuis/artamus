package music.primitives

final case class Window(start: Position, end: Position) {

  def duration: Duration = end - start
  def durationUntil(that: Window): Duration = that.start - end
  def intersect(that: Window): Option[Window] = {
    if (that.start > this.end || this.start > that.end) None
    else Some(Window(Seq(this.start, that.start).max, Seq(this.end, that.end).min))
  }

}

object Window {

  def apply(start: Position, duration: Duration): Window = Window(start, Position(start.value + duration.value))

}