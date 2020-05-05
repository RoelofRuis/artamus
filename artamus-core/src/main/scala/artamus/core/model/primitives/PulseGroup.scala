package artamus.core.model.primitives

import artamus.core.math.FractionalPowerOfTwo

final case class PulseGroup(
  baseDuration: FractionalPowerOfTwo,
  numberOfBeats: Int
)
