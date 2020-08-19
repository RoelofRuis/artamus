package nl.roelofruis.artamus.degree

import nl.roelofruis.artamus.degree.FileModel.TextTuning
import nl.roelofruis.artamus.degree.Model.{Chord, Degree, PitchDescriptor, Quality}

object Write {

  implicit class TuningWriteOps(tuning: TextTuning) {

    def printChords(chords: Seq[Chord]): String = {
      chords.map { chord =>
        printNoteDescriptor(chord.root) + printQuality(chord.quality)
      }.mkString("\n")
    }

    def printDegrees(degrees: Seq[Degree]): String = {
      degrees.map { degree =>
        printDegreeDescriptor(degree.root) + printQuality(degree.quality)
      }.mkString("\n")
    }

    def printQuality(quality: Quality): String = {
      quality.intervals.toString
    }

    def printDegreeDescriptor(descriptor: PitchDescriptor): String = {
      val base = tuning.textDegrees(descriptor.step)
      val diff = Seq(
        descriptor.pitchClass,
        descriptor.pitchClass + tuning.numPitchClasses,
        descriptor.pitchClass - tuning.numPitchClasses
      )
        .map(_ - tuning.pitchClassSequence(descriptor.step))
        .minBy(Math.abs)

      if (diff > 0) (tuning.textSharp * diff) + base
      else if (diff < 0) (tuning.textFlat * -diff) + base
      else base
    }

    def printNoteDescriptor(descriptor: PitchDescriptor): String = {
      val base = tuning.textNotes(descriptor.step)
      val diff = Seq(
        descriptor.pitchClass,
        descriptor.pitchClass + tuning.numPitchClasses,
        descriptor.pitchClass - tuning.numPitchClasses
      )
        .map(_ - tuning.pitchClassSequence(descriptor.step))
        .minBy(Math.abs)

      if (diff > 0) base + (tuning.textSharp * diff)
      else if (diff < 0) base + (tuning.textFlat * -diff)
      else base
    }

  }

}
