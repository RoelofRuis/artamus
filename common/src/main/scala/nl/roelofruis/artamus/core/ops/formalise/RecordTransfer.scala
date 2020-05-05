package nl.roelofruis.artamus.core.ops.formalise

import nl.roelofruis.math.Rational
import nl.roelofruis.math.temporal.Duration

final case class RecordTransfer(
  quantizer: Quantizer,
  rhythmOnly: Boolean,
  lastNoteDuration: Duration = Duration(Rational(1, 4)) // TODO: Removed after support for quantizing full notes
)
