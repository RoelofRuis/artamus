package nl.roelofruis.artamus.analysis

import nl.roelofruis.artamus.degree.Model._

trait TuningMaths {
  val tuning: Tuning

  def numSteps: Int = tuning.pitchClassSequence.size

  implicit class PitchDescriptorMath(descr: PitchDescriptor) {
    def +(that: PitchDescriptor): PitchDescriptor = {
      val targetStep = (descr.step + that.step) % numSteps
      val targetPitchClass = (descr.pitchClass + that.pitchClass) % tuning.numPitchClasses

      PitchDescriptor(targetStep, targetPitchClass)
    }

    def -(that: PitchDescriptor): PitchDescriptor = {
      val targetStep = ((descr.step - that.step) + numSteps) % numSteps
      val targetPitchClass = ((descr.pitchClass - that.pitchClass) + tuning.numPitchClasses) % tuning.numPitchClasses

      PitchDescriptor(targetStep, targetPitchClass)
    }
  }

  implicit class KeyMath(key: Key) {
    def contains(chord: Chord): Boolean = {
      val relativeRoot = chord.root - key.root
      key.scale.hasQualityAtPitchClass(relativeRoot.pitchClass, chord.quality)
    }
  }

  implicit class ScaleMath(scale: Scale) {
    def numSteps: Int = scale.pitchClassSequence.size

    def hasQualityAtPitchClass(pitchClass: Int, quality: Quality): Boolean = {
      val initialStep = scale.pitchClassSequence.indexOf(pitchClass)
      if (initialStep < 0) false
      else {
        quality.intervals.drop(1).foldRight(true) { case (descriptor, valid) =>
          val nextStep = (initialStep + descriptor.step) % numSteps
          val nextPc = (pitchClass + descriptor.pitchClass) % tuning.numPitchClasses
          valid && (scale.pitchClassSequence(nextStep) == nextPc)
        }
      }
    }

    def asPitchDescriptors: List[PitchDescriptor] = {
      scale.pitchClassSequence.zipWithIndex.map { case (pitchClass, step) => PitchDescriptor(step, pitchClass) }
    }
  }
}
