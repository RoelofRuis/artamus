package music.symbols

import music.collection.SymbolProperties
import music.primitives.TimeSignatureDivision

case object TimeSignature extends SymbolType {

  def apply(division: TimeSignatureDivision): SymbolProperties[TimeSignature.type] =
    SymbolProperties[TimeSignature.type]
      .add(division)


}