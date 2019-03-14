package application

package object model {

  case class ID(id: Long) extends AnyVal

  case class Ticks(value: Long) extends AnyVal

  case class Note(pitch: Int, volume: Int)

  case class TimeSpan(start: Ticks, duration: Ticks) {
    def end: Ticks = Ticks(start.value + duration.value)
  }

  case class Measure[A](baseNote: Int, baseNotesPerMeasure: Int) {
    // TODO: implement
  }

}
