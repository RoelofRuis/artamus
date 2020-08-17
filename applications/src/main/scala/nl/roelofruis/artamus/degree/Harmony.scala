package nl.roelofruis.artamus.degree

import nl.roelofruis.artamus.degree.FileModel.TextTuning
import nl.roelofruis.artamus.degree.Model._

object Harmony {

  implicit class TuningOps(tuning: TextTuning) {

    implicit class PitchDescriptorOps(descr: PitchDescriptor) {
      def +(that: PitchDescriptor): PitchDescriptor = {
        val targetStep = (descr.step + that.step) % tuning.pitchClassSequence.size
        val targetPitchClass = (descr.pitchClass + that.pitchClass) % tuning.numPitchClasses

        PitchDescriptor(targetStep, targetPitchClass)
      }
    }

    def numSteps: Int = tuning.pitchClassSequence.size

    def nameChords(degrees: List[Degree], root: PitchDescriptor): List[Chord] = {
      degrees.map { degree =>
        val chordPitch = degree.pitch + root
        Chord(chordPitch, degree.quality)
      }
    }

  }

}
