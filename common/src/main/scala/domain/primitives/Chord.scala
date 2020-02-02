package domain.primitives

final case class Chord(
  root: PitchClass,
  functions: Set[Function]
)
