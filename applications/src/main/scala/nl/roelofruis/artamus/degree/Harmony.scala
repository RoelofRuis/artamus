package nl.roelofruis.artamus.degree

import nl.roelofruis.artamus.degree.FileModel.TextTuning
import nl.roelofruis.artamus.degree.Model._

object Harmony {

  implicit class TuningOps(tuning: TextTuning) {

    implicit class PitchDescriptorOps(descr: PitchDescriptor) {
      def +(that: PitchDescriptor): PitchDescriptor = {
        val targetStep = (descr.step + that.step) % tuning.numSteps
        val targetPitchClass = (descr.pitchClass + that.pitchClass) % tuning.numPitchClasses

        PitchDescriptor(targetStep, targetPitchClass)
      }

      def -(that: PitchDescriptor): PitchDescriptor = {
        val targetStep = ((descr.step - that.step) + tuning.numSteps) % tuning.numSteps
        val targetPitchClass = ((descr.pitchClass - that.pitchClass) + tuning.numPitchClasses) % tuning.numPitchClasses

        PitchDescriptor(targetStep, targetPitchClass)
      }
    }

    def numSteps: Int = tuning.pitchClassSequence.size

    def nameChords(degrees: Seq[Degree], root: PitchDescriptor): Seq[Chord] = {
      degrees.map { degree =>
        val chordPitch = degree.root + root
        Chord(chordPitch, degree.quality)
      }
    }

    def nameDegrees(chords: Seq[Chord], key: Key): Seq[Degree] = {
      chords.map { chord =>
        // TODO: return option based on whether the quality matches the scale.
        val rootPitch = chord.root - key.root
        val scalePitchClass = key.scale.pitchClassSequence(rootPitch.step)
        val pitchClassDifference = rootPitch.pitchClass - scalePitchClass

        val tuningPitch = PitchDescriptor(rootPitch.step, tuning.pitchClassSequence(rootPitch.step))
        val degreePitch = tuningPitch + PitchDescriptor(0, pitchClassDifference)

        Degree(degreePitch, chord.quality)
      }
    }

  }

}
