package application

import scala.reflect.ClassTag

package object model {

  case class ID[C: ClassTag](id: Long)

  case class Ticks(value: Long) extends AnyVal

  case class Note(pitch: Int, volume: Int)

  case class TimeSpan(start: Ticks, duration: Ticks) {
    def end: Ticks = Ticks(start.value + duration.value)
  }

  case class Measure(baseNote: Int, baseNotesPerMeasure: Int) {
    // TODO: implement
  }

}
