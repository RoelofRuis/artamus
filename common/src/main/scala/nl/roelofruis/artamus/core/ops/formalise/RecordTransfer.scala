package nl.roelofruis.artamus.core.ops.formalise

import domain.math.Rational
import domain.math.temporal.Duration

final case class RecordTransfer(
  quantizer: Quantizer,
  rhythmOnly: Boolean,
  lastNoteDuration: Duration = Duration(Rational(1, 4)) // TODO: Removed after support for quantizing full notes
)
