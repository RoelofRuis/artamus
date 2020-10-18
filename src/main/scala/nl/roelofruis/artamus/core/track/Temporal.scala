package nl.roelofruis.artamus.core.track

import nl.roelofruis.artamus.core.common.Maths.FractionalPowerOfTwo

object Temporal {

  final case class Metre(
    pulseGroups: Seq[PulseGroup]
  )

  type BeatGroup = Int
  object BeatGroup {
    val Single: BeatGroup = 1
    val Double: BeatGroup = 2
    val Triple: BeatGroup = 3
  }

  final case class PulseGroup(
    baseDuration: FractionalPowerOfTwo,
    numberOfBeats: BeatGroup
  )

}
