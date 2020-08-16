package nl.roelofruis.artamus.degree

object Model {

  final case class ExpansionRule(
    base: Degree,
    expansion: List[Degree]
  )

  final case class Degree(
    pitchClass: Int,
    step: Int
  )

  final case class PitchSpelling(
    step: Int,
    accidental: Int
  )

  final case class Scale(
    steps: List[Int]
  )

  final case class Key(
    root: PitchSpelling,
    scale: Scale
  )

  final case class ChordQuality(
    name: String
  )

  final case class Chord(
    root: PitchSpelling,
    quality: ChordQuality
  )

}
