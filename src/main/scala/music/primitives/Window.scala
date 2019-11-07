package music.primitives

final case class Window(start: Position, duration: Duration) {

  def end: Position = start + duration

  def until(that: Window): Option[Window] = {
    val durationDiff = that.start - end
    if (durationDiff == Duration.NONE) None
    else Some(Window(end, durationDiff))
  }

  def intersect(that: Window): Option[Window] = {
    if (that.start > this.end || this.start > that.end) None
    else {
      val largestStart = Seq(this.start, that.start).max
      val smallestEnd = Seq(this.end, that.end).min
      Some(Window(largestStart, smallestEnd - largestStart))
    }
  }

}

object Window {

  def instantAt(pos: Position): Window = Window(pos, Duration.NONE)

}