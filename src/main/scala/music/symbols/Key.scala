package music.symbols

import music.collection.SymbolProperties
import music.primitives.Scale
import music.spelling.SpelledPitch

trait Key extends SymbolType

object Key {

  def apply(root: SpelledPitch, scale: Scale): SymbolProperties[Key] =
    SymbolProperties[Key]
      .add(root)
      .add(scale)

}