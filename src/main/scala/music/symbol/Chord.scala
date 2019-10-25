package music.symbol

import music.primitives.{Duration, Function, PitchClass, PitchSpelling}

import scala.collection.immutable.SortedSet

final case class Chord(
  root: PitchClass,
  functions: SortedSet[Function],
  duration: Option[Duration],
  rootSpelling: Option[PitchSpelling]
) extends SymbolType {

  override def getDuration: Duration = duration.getOrElse(Duration.zero)

  def withDuration(dur: Duration): Chord = this.copy(duration = Some(dur))

  def withRootSpelling(spelling: PitchSpelling): Chord = this.copy(rootSpelling = Some(spelling))

}

object Chord {

  def apply(
    root: PitchClass,
    functions: SortedSet[Function]
  ): Chord = Chord(root, functions, None, None)

}