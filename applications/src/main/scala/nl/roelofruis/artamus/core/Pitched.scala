package nl.roelofruis.artamus.core

object Pitched {

  final case class PitchDescriptor(
    step: Int,
    pitchClass: Int
  )

  final case class Degree(
    root: PitchDescriptor,
    quality: Quality,
    relativeTo: Option[PitchDescriptor] = None,
    tritoneSub: Boolean = false
  )

  final case class Quality(
    intervals: Seq[PitchDescriptor]
  )

  final case class Chord(
    root: PitchDescriptor,
    quality: Quality
  )

  final case class Scale(
    pitchClassSequence: Seq[Int]
  )

  final case class Key(
    root: PitchDescriptor,
    scale: Scale
  )

}