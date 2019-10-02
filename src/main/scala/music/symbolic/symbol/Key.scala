package music.symbolic.symbol

import music.symbolic.pitch.{Scale, SpelledPitch}

final case class Key(root: SpelledPitch, scale: Scale)
