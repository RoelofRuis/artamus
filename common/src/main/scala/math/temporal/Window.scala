package math.temporal

/** A time window expressed by a position with a duration. The duration might be zero, expressing an instantaneous
  * moment.
  *
  * @param start The window start position.
  * @param duration The duration.
  */
final case class Window(start: Position, duration: Duration) {

  def end: Position = start + duration

  def until(that: Window): Option[Window] = {
    val durationDiff = that.start - end
    if (durationDiff == Duration.ZERO) None
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

  def spanning(that: Window): Window = {
    val smallestStart = Seq(this.start, that.start).min
    val largestEnd = Seq(this.end, that.end).max
    Window(smallestStart, largestEnd - smallestStart)
  }

  def isInstant: Boolean = duration == Duration.ZERO

}

object Window {

  def instantAt(pos: Position): Window = Window(pos, Duration.ZERO)

}