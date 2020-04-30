package domain.display.layout

import domain.math.temporal.Position
import domain.primitives.{Metre, PulseGroup}

// TODO: this should integrate with current [[domain.write.TimeSignatures]]
final case class Metres() {

  def iterateMetres: LazyList[WindowedMetre] = {
    val activeMetre = Metre(Seq(PulseGroup(2, 2), PulseGroup(2, 2)))

    def loop(searchPos: Position): LazyList[WindowedMetre] = {
      WindowedMetre(searchPos, activeMetre) #:: loop(searchPos + activeMetre.duration)
    }

    loop(Position.ZERO)
  }

}
