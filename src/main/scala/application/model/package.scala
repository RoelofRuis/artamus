package application

package object model {

  case class Ticks(value: Long) extends AnyVal

  case class Note(pitch: Int, volume: Int)

  case class TimeSpan(start: Ticks, duration: Ticks)

  case class Track[A](ticksPerQuarter: Ticks, elements: Iterable[(TimeSpan, A)]) {

    // TODO: implement convenience methods

  }

}
