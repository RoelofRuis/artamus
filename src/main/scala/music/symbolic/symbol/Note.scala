package music.symbolic.symbol

import music.symbolic.pitch.{ExactPitch, Pitch}
import music.symbolic.temporal.Duration

/** @deprecated, this should find itself defined by combination of properties */
final case class Note[A <: ExactPitch](duration: Duration, pitch: Pitch[A])
