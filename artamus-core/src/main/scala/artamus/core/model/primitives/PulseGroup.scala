package artamus.core.model.primitives

import nl.roelofruis.math.FractionalPowerOfTwo

final case class PulseGroup(
  baseDuration: FractionalPowerOfTwo,
  numberOfBeats: Int
)
