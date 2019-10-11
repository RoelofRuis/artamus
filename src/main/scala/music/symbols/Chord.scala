package music.symbols

import music.collection.SymbolProperties
import music.primitives.{ChordFunctions, ChordRoot, Function, PitchClass}

import scala.collection.immutable.SortedSet

case object Chord extends SymbolType {
  def apply(root: PitchClass, functions: SortedSet[Function]): SymbolProperties[Chord.type] =
    SymbolProperties[Chord.type]
      .add(ChordRoot(root))
      .add(ChordFunctions(functions))
}