package music.symbol

import music.primitives.TimeSignatureDivision

final case class TimeSignature(
  division: TimeSignatureDivision
) extends SymbolType
