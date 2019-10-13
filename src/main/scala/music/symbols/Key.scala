package music.symbols

import music.collection.SymbolProperties
import music.primitives.Scale
import music.spelling.SpelledPitch

// TODO: split these up!
case object Key extends SymbolType {

  def apply(root: SpelledPitch, scale: Scale): SymbolProperties[Key.type] =
    SymbolProperties[Key.type]
      .add(root)
      .add(scale)

}