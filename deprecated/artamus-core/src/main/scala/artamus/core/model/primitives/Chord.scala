package artamus.core.model.primitives

final case class Chord(
  root: PitchClass,
  functions: Set[Function]
)
