package artamus.core.model.track

import artamus.core.math.temporal.{Position, Window}
import artamus.core.model.primitives.Degree

import scala.collection.immutable.SortedMap

final case class Degrees private(
  degrees: SortedMap[Position, (Window, Degree)]
) {

  def writeDegree(window: Window, degree: Degree): Degrees = {
    copy(degrees.updated(window.start, (window, degree)))
  }

}

object Degrees {

  def apply(): Degrees = new Degrees(SortedMap())

}
