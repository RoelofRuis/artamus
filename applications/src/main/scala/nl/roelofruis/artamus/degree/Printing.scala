package nl.roelofruis.artamus.degree

import nl.roelofruis.artamus.degree.FileModel.TextTuning
import nl.roelofruis.artamus.degree.Model.{Chord, PitchDescriptor}

object Printing {

  implicit class TextOps(tuning: TextTuning) {

    def printChords(chords: List[Chord]): String = {
      chords.map { chord => printPitchDescriptor(chord.root) }.mkString("\n")
    }

    def printPitchDescriptor(descriptor: PitchDescriptor): String = {
      val base = tuning.noteNames(descriptor.step)
      val diff = descriptor.pitchClass - tuning.pitchClassSequence(descriptor.step)
      if (diff > 0) base + (tuning.textSharp * diff)
      else if (diff < 0) base + (tuning.textFlat * -diff)
      else base
    }

  }

}
