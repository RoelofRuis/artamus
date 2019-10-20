package music.symbols

import music.primitives.{Duration, Function, PitchClass}

import scala.collection.immutable.SortedSet

final case class Chord(
  root: PitchClass,
  functions: SortedSet[Function],
  duration: Option[Duration]
) extends SymbolType {

  def withDuration(dur: Duration): Chord = this.copy(duration = Some(dur))

}

object Chord {

  def apply(
    root: PitchClass,
    functions: SortedSet[Function]
  ): Chord = Chord(root, functions, None)

}