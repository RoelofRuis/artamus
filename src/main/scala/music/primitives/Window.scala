package music.primitives

final case class Window private(start: Position, end: Position) {

  def duration: Duration = end - start
  def until(that: Window): Option[Window] = {
    if (that.start <= end) None
    else Some(Window.between(end, that.start))
  }
  def intersect(that: Window): Option[Window] = {
    if (that.start > this.end || this.start > that.end) None
    else Some(Window.between(Seq(this.start, that.start).max, Seq(this.end, that.end).min))
  }

}

object Window {

  def between(start: Position, end: Position): Window = Window(start, end)
  def instantAt(pos: Position): Window = Window(pos, pos)

}