package nl.roelofruis.artamus.degree

object Model {

  final case class ExpansionRule(
    base: Degree,
    expansion: List[Degree]
  )

  final case class Degree(
    pitch: PitchDescriptor,
  )

  final case class PitchDescriptor(
    step: Int,
    pitchClass: Int
  )

  final case class Chord(
    root: PitchDescriptor,
  )

}
