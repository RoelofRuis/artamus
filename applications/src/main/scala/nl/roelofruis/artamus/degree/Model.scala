package nl.roelofruis.artamus.degree

object Model {

  final case class PitchDescriptor(
    step: Int,
    pitchClass: Int
  )

  final case class Degree(
    root: PitchDescriptor,
  )

  final case class Interval(
    size: PitchDescriptor
  )

  final case class Chord(
    root: PitchDescriptor,
  )

}
