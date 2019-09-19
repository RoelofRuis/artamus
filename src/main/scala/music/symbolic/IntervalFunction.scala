package music.symbolic

final case class IntervalFunction(interval: Interval) extends Comparable[IntervalFunction] {
  override def compareTo(o: IntervalFunction): Int = {
    interval.musicVector compareTo o.interval.musicVector
  }
}