package domain.write

import domain.math.temporal.Position
import domain.primitives.Metre

import scala.collection.immutable.SortedMap

final case class Metres private (
  metres: SortedMap[Position, Metre]
) {

  def writeMetre(pos: Position, metre: Metre): Metres = copy(metres = metres.updated(pos, metre))

}

object Metres {

  def apply(): Metres = {
    new Metres(SortedMap(Position.ZERO -> Metre.`4/4`))
  }

}
