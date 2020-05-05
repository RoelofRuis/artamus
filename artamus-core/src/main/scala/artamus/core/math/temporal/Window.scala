package artamus.core.math.temporal

/** A time window expressed by a position with a duration. The duration might be zero, expressing an instantaneous
  * moment.
  *
  * In mathematics this is also often called an **interval** but this clashes with the musical meaning of Interval.
  *
  * @param start The window start position.
  * @param duration The duration.
  */
final case class Window(start: Position, duration: Duration) {

  /** @return The absolute position that this window ends*/
  def end: Position = start + duration

  /**
   * Calculates the window spanning the duration from the end of this window until the start of `that` window. If no
   * such window exists returns None.
   *
   * @param that Window
   * @return Option[Window]
   */
  def until(that: Window): Option[Window] = {
    val durationDiff = that.start - end
    if (durationDiff == Duration.ZERO) None
    else Some(Window(end, durationDiff))
  }

  /**
   * Calculates the window that is the intersection of this window with `that` window. Returns an 'instant' window if
   * this window ends the moment `that` window starts. If no intersection exists returns None.
   *
   * @param that Window
   * @return Option[Window]
   */
  def intersect(that: Window): Option[Window] = {
    if (that.start > this.end || this.start > that.end) None
    else {
      val largestStart = Seq(this.start, that.start).max
      val smallestEnd = Seq(this.end, that.end).min
      Some(Window(largestStart, smallestEnd - largestStart))
    }
  }

  /**
   * Calculates the window that is the intersection of this window with `that` window. If no such intersection exists,
   * or the intersection would be an instant, returns None.
   * @param that Window
   * @return Option[Window]
   */
  def intersectNonInstant(that: Window): Option[Window] = {
    intersect(that).flatMap(i => if (i.isInstant) None else Some(i))
  }

  /**
   * Calculates the window spanning the smallest start of this window and `that` window and the largest end
   * of this window and `that` window.
   *
   * @param that Window
   * @return Window
   */
  def spanning(that: Window): Window = {
    val smallestStart = Seq(this.start, that.start).min
    val largestEnd = Seq(this.end, that.end).max
    Window(smallestStart, largestEnd - smallestStart)
  }

  /** @return Boolean Whether this window has zero duration. */
  def isInstant: Boolean = duration == Duration.ZERO

  /** @return Whether this window ends later than `that` window */
  def endsLaterThan(that: Window): Boolean = end > that.end

}

object Window {

  def instantAt(pos: Position): Window = Window(pos, Duration.ZERO)

}