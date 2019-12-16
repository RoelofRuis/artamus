package music.domain.primitives

final case class Chord(
  root: PitchClass,
  functions: Set[Function]
)
