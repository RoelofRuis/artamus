package domain.primitives

import domain.math.FractionalPowerOfTwo

final case class PulseGroup(
  baseDuration: FractionalPowerOfTwo,
  numberOfBeats: Int
)
