package nl.roelofruis.artamus.application

import nl.roelofruis.artamus.core.track.Pitched._
import nl.roelofruis.artamus.application.Model.Settings

object Printer {

  implicit class WriteOps(settings: Settings) {
    def printChords(chords: Seq[Chord]): String = chords.map(printChord).mkString(" ")

    def printChord(chord: Chord): String = printNoteDescriptor(chord.root) + printQuality(chord.quality)

    def printDegrees(degrees: Seq[Degree]): String = degrees.map(printDegree).mkString(" ")

    def printDegree(degree: Degree): String = {
      val descriptor = printDegreeDescriptor(degree.root) + printQualityGroup(degree.quality)
      val tritoneSub = if (degree.tritoneSub) "T" else ""
      degree.relativeTo match {
        case None => s"$descriptor$tritoneSub"
        case Some(relative) => s"$descriptor$tritoneSub/${printDegreeDescriptor(relative)}"
      }
    }

    def printQualityGroup(qualityGroup: QualityGroup): String = settings.qualityGroupMap.map(_.swap).getOrElse(qualityGroup, "?")

    def printQuality(quality: Quality): String = settings.qualityMap.map(_.swap).getOrElse(quality, "?")

    def printKeyDegree(key: Key): String= settings.printDegreeDescriptor(key.root) + " " + settings.printScale(key.scale)

    def printKey(key: Key): String = settings.printNoteDescriptor(key.root) + " " + settings.printScale(key.scale)

    def printScale(scale: Scale): String = settings.scaleMap.map(_.swap).getOrElse(scale, "?")

    def printIntervalDescriptor(descriptor: PitchDescriptor): String = {
      val base = ObjectParsers.INTERVALS(descriptor.step)
      val diff = pitchClassDifference(descriptor)

      if (diff > 0) (ObjectParsers.SHARP * diff) + base
      else if (diff < 0) (ObjectParsers.FLAT * -diff) + base
      else base
    }

    def printDegreeDescriptor(descriptor: PitchDescriptor): String = {
      val base = ObjectParsers.DEGREES(descriptor.step)
      val diff = pitchClassDifference(descriptor)

      if (diff > 0) (ObjectParsers.SHARP * diff) + base
      else if (diff < 0) (ObjectParsers.FLAT * -diff) + base
      else base
    }

    def printNoteDescriptor(descriptor: PitchDescriptor): String = {
      val base = ObjectParsers.NOTES(descriptor.step)
      val diff = pitchClassDifference(descriptor)

      if (diff > 0) base + (ObjectParsers.SHARP * diff)
      else if (diff < 0) base + (ObjectParsers.FLAT * -diff)
      else base
    }

    private def pitchClassDifference(descriptor: PitchDescriptor): Int = Seq(
        descriptor.pitchClass,
        descriptor.pitchClass + settings.numPitchClasses,
        descriptor.pitchClass - settings.numPitchClasses
      )
        .map(_ - settings.pitchClassSequence(descriptor.step))
        .minBy(Math.abs)
  }

}
