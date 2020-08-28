package nl.roelofruis.artamus.core

import nl.roelofruis.artamus.core.math.FractionalPowerOfTwo

object Temporal {

  final case class PulseGroup(
    baseDuration: FractionalPowerOfTwo,
    numberOfBeats: Int
  )

  final case class Metre(
    pulseGroups: Seq[PulseGroup]
  )

}
