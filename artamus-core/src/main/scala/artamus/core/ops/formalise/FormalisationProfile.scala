package artamus.core.ops.formalise

import artamus.core.math.Rational
import artamus.core.math.temporal.Duration

final case class FormalisationProfile(
  quantizer: Quantizer,
  rhythmOnly: Boolean,
  lastNoteDuration: Duration = Duration(Rational(1, 4)) // TODO: Removed after support for quantizing full notes
)
