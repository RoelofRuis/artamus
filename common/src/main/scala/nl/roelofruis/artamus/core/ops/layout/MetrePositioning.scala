package nl.roelofruis.artamus.core.ops.layout

import nl.roelofruis.math.temporal.{Position, Window}
import nl.roelofruis.artamus.core.model.primitives.Metre
import nl.roelofruis.artamus.core.model.track.Metres

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
