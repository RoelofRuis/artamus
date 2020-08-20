package nl.roelofruis.artamus.degree

object Model {

  final case class PitchDescriptor(
    step: Int,
    pitchClass: Int
  )

  final case class Degree(
    root: PitchDescriptor,
    quality: Quality
  )

  final case class Quality(
    intervals: List[PitchDescriptor]
  )

  final case class Chord(
    root: PitchDescriptor,
    quality: Quality
  )

  final case class Scale(
    pitchClassSequence: List[Int]
  )

  final case class Key(
    root: PitchDescriptor,
    scale: Scale
  )

}
