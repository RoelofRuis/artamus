package application.model.event

import scala.reflect.{ClassTag, classTag}

package object domain {

  case class ID[C: ClassTag](id: Long) {
    override def toString: String = s"${classTag[C].runtimeClass.getSimpleName}($id)"
  }

  case class Ticks(value: Long) extends AnyVal

  case class Note(pitch: Int, volume: Int)

  case class TimeSpan(start: Ticks, duration: Ticks) {
    def end: Ticks = Ticks(start.value + duration.value)
  }

}
