package music.symbols

import music.primitives.Scale
import music.spelling.SpelledPitch

final case class Key(
  root: SpelledPitch,
  scale: Scale
) extends SymbolType
