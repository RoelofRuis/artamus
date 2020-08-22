package nl.roelofruis.artamus.degree

import nl.roelofruis.artamus.tuning.Parser.MusicObjectsParser

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
    intervals: Seq[PitchDescriptor]
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

  final case class Tuning(
    pitchClassSequence: List[Int],
    numPitchClasses: Int,
    textNotes: List[String],
    textIntervals: List[String],
    textSharp: String,
    textFlat: String,
    textDegrees: List[String],
    scaleMap: Map[String, Scale],
    qualityMap: Map[String, Quality],
  ) extends MusicObjectsParser

}
