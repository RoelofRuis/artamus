package artamus.core.model.track

import artamus.core.math.temporal.Position
import artamus.core.model.primitives.{Metre, PulseGroup}

import scala.collection.immutable.SortedMap

final case class Metres private (
  metres: SortedMap[Position, Metre]
) {

  def writeMetre(pos: Position, metre: Metre): Metres = copy(metres = metres.updated(pos, metre))

}

object Metres {

  def apply(): Metres = {
    new Metres(SortedMap(Position.ZERO -> Metre(Seq(PulseGroup(2, 2), PulseGroup(2, 2)))))
  }

}
