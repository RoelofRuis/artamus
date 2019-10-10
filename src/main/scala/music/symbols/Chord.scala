package music.symbols

import music.collection.SymbolProperties
import music.primitives.{ChordFunctions, ChordRoot, PitchClass, Function}

case object Chord extends SymbolType {
  def apply(root: PitchClass, functions: Seq[Function]): SymbolProperties[Chord.type] =
    SymbolProperties.empty[Chord.type]
      .add(ChordRoot(root))
      .add(ChordFunctions(functions))
}