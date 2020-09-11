package nl.roelofruis.artamus.core

import nl.roelofruis.artamus.core.Containers.Positioned
import nl.roelofruis.artamus.core.Maths.FractionalPowerOfTwo

object Temporal {

  final case class Metre(
    pulseGroups: Seq[PulseGroup]
  )

  final case class PulseGroup(
    baseDuration: FractionalPowerOfTwo,
    numberOfBeats: Int
  )

  type MetreSequence = Seq[Positioned[Metre]]

}
