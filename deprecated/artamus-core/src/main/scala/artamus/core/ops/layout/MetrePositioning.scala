package artamus.core.ops.layout

import artamus.core.math.temporal.{Position, Window}
import artamus.core.model.primitives.Metre
import artamus.core.model.track.Metres

object MetrePositioning {

  final case class PositionedMetre(
    position: Position,
    metre: Metre
  ) {
    lazy val window: Window = Window(position, metre.duration)
  }

  implicit class MetresPositioning(metres: Metres) {
    def iteratePositioned: LazyList[PositionedMetre] = {
      val (_, activeMetre) = metres.metres.head

      def loop(searchPos: Position): LazyList[PositionedMetre] = {
        PositionedMetre(searchPos, activeMetre) #:: loop(searchPos + activeMetre.duration)
      }

      loop(Position.ZERO)
    }
  }
}
