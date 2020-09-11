package nl.roelofruis.artamus.core.track

import nl.roelofruis.artamus.core.common.Maths.FractionalPowerOfTwo
import nl.roelofruis.artamus.core.common.Containers.Positioned

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
