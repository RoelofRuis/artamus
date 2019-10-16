package music.symbols

import music.collection.SymbolProperties
import music.primitives.{ChordFunctions, ChordRoot, Function, PitchClass}

import scala.collection.immutable.SortedSet

trait Chord extends SymbolType

object Chord {
  def apply(root: PitchClass, functions: SortedSet[Function]): SymbolProperties[Chord] =
    SymbolProperties[Chord]
      .add(ChordRoot(root))
      .add(ChordFunctions(functions))
}