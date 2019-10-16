package music.symbols

import music.collection.SymbolProperties
import music.primitives.TimeSignatureDivision

trait TimeSignature extends SymbolType

object TimeSignature {

  def apply(division: TimeSignatureDivision): SymbolProperties[TimeSignature] =
    SymbolProperties[TimeSignature]
      .add(division)


}