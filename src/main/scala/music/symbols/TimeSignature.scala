package music.symbols

import music.primitives.TimeSignatureDivision

final case class TimeSignature(
  division: TimeSignatureDivision
) extends SymbolType
