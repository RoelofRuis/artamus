package music.symbols

import music.primitives.{PitchSpelling, Scale}

final case class Key(
  root: PitchSpelling,
  scale: Scale
) extends SymbolType
