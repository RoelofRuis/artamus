package music.symbolic

import music.symbolic.pitched.{ExactPitch, Pitch}

final case class Note[A <: ExactPitch](duration: Duration, pitch: Pitch[A])
