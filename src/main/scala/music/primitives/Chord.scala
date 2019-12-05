package music.primitives

final case class Chord(
  root: PitchClass,
  functions: Set[Function],
  rootSpelling: Option[PitchSpelling]
) {

  def withRootSpelling(spelling: PitchSpelling): Chord = this.copy(rootSpelling = Some(spelling))

}

object Chord {

  def apply(
    root: PitchClass,
    functions: Set[Function]
  ): Chord = Chord(root, functions, None)

}