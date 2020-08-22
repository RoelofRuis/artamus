package nl.roelofruis.artamus.tuning

import nl.roelofruis.artamus.degree.Model._

object Printer {

  implicit class TuningWriteOps(tuning: Tuning) {
    def printChords(chords: Seq[Chord]): String = chords.map(printChord).mkString(" ")

    def printChord(chord: Chord): String = printNoteDescriptor(chord.root) + printQuality(chord.quality)

    def printDegrees(degrees: Seq[Degree]): String = degrees.map(printDegree).mkString(" ")

    def printDegree(degree: Degree): String = printDegreeDescriptor(degree.root) + printQuality(degree.quality)

    def printQuality(quality: Quality): String = tuning.qualityMap.map(_.swap).getOrElse(quality, "?")

    def printKey(key: Key): String = tuning.printNoteDescriptor(key.root) + " " + tuning.printScale(key.scale)

    def printScale(scale: Scale): String = tuning.scaleMap.map(_.swap).getOrElse(scale, "?")

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
