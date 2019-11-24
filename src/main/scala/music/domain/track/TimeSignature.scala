package music.domain.track

import music.primitives.TimeSignatureDivision
import music.domain.track.symbol.SymbolType

final case class TimeSignature(
  division: TimeSignatureDivision
) extends SymbolType
