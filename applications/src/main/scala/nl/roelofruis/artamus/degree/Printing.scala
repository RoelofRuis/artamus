package nl.roelofruis.artamus.degree

import nl.roelofruis.artamus.degree.FileModel.TextTuning
import nl.roelofruis.artamus.degree.Model.{Chord, PitchDescriptor}

object Printing {

  implicit class TextOps(tuning: TextTuning) {

    def printChords(chords: Seq[Chord]): String = {
      chords.map { chord => printPitchDescriptor(chord.root) }.mkString("\n")
    }

    def printPitchDescriptor(descriptor: PitchDescriptor): String = {
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
