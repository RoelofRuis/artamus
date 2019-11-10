package music.symbol

import music.primitives.{Function, PitchClass, PitchSpelling}

import scala.collection.immutable.SortedSet

final case class Chord(
  root: PitchClass,
  functions: SortedSet[Function],
  rootSpelling: Option[PitchSpelling]
) extends SymbolType {

  def withRootSpelling(spelling: PitchSpelling): Chord = this.copy(rootSpelling = Some(spelling))

}

object Chord {

  def apply(
    root: PitchClass,
    functions: SortedSet[Function]
  ): Chord = Chord(root, functions, None)

}