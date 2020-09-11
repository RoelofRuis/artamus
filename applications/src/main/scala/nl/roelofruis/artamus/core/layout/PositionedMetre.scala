package nl.roelofruis.artamus.core.layout

import nl.roelofruis.artamus.core.Temporal.{Metre, MetreSequence}
import nl.roelofruis.artamus.core.analysis.TemporalMaths
import nl.roelofruis.artamus.core.primitives.{Position, Window}

final case class PositionedMetre(
  position: Position,
  metre: Metre
)

object PositionedMetre extends TemporalMaths {

  implicit class PositionedMetreOps(positionedMetre: PositionedMetre) {
    lazy val window: Window = Window(positionedMetre.position, positionedMetre.metre.duration)
  }

  implicit class MetresPositioning(metres: MetreSequence) {
    def iteratePositioned: LazyList[PositionedMetre] = {
      val activeMetre = metres.head.element

      def loop(searchPos: Position): LazyList[PositionedMetre] = {
        PositionedMetre(searchPos, activeMetre) #:: loop(searchPos + activeMetre.duration)
      }

      loop(Position.ZERO)
    }
  }

}
