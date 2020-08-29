package nl.roelofruis.artamus.application

import nl.roelofruis.artamus.core.Pitched._
import nl.roelofruis.artamus.application.Model.Settings

object Printer {

  implicit class TuningWriteOps(tuning: Settings) {
    def printChords(chords: Seq[Chord]): String = chords.map(printChord).mkString(" ")

    def printChord(chord: Chord): String = printNoteDescriptor(chord.root) + printQuality(chord.quality)

    def printDegrees(degrees: Seq[Degree]): String = degrees.map(printDegree).mkString(" ")

    def printDegree(degree: Degree): String = {
      val descriptor = printDegreeDescriptor(degree.root) + printQuality(degree.quality)
      val tritoneSub = if (degree.tritoneSub) "T" else ""
      degree.relativeTo match {
        case None => descriptor
        case Some(relative) => descriptor + tritoneSub + "/" + printDegreeDescriptor(relative)
      }
    }

    def printQuality(quality: Quality): String = tuning.qualityMap.map(_.swap).getOrElse(quality, "?")

    def printKey(key: Key): String = tuning.printNoteDescriptor(key.root) + " " + tuning.printScale(key.scale)

    def printScale(scale: Scale): String = tuning.scaleMap.map(_.swap).getOrElse(scale, "?")

    def printIntervalDescriptor(descriptor: PitchDescriptor): String = {
      val base = tuning.textIntervals(descriptor.step)
      val diff = pitchClassDifference(descriptor)

      if (diff > 0) (tuning.textSharp * diff) + base
      else if (diff < 0) (tuning.textFlat * -diff) + base
      else base
    }

    def printDegreeDescriptor(descriptor: PitchDescriptor): String = {
      val base = tuning.textDegrees(descriptor.step)
      val diff = pitchClassDifference(descriptor)

      if (diff > 0) (tuning.textSharp * diff) + base
      else if (diff < 0) (tuning.textFlat * -diff) + base
      else base
    }

    def printNoteDescriptor(descriptor: PitchDescriptor): String = {
      val base = tuning.textNotes(descriptor.step)
      val diff = pitchClassDifference(descriptor)

      if (diff > 0) base + (tuning.textSharp * diff)
      else if (diff < 0) base + (tuning.textFlat * -diff)
      else base
    }

    private def pitchClassDifference(descriptor: PitchDescriptor): Int = Seq(
        descriptor.pitchClass,
        descriptor.pitchClass + tuning.numPitchClasses,
        descriptor.pitchClass - tuning.numPitchClasses
      )
        .map(_ - tuning.pitchClassSequence(descriptor.step))
        .minBy(Math.abs)

  }

}
