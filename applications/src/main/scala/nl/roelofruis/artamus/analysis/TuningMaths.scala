package nl.roelofruis.artamus.analysis

import nl.roelofruis.artamus.degree.Model._

trait TuningMaths {
  val tuning: Tuning

  lazy val numSteps: Int = tuning.pitchClassSequence.size

  lazy val getAllPitchDescriptors: Seq[PitchDescriptor] = tuning
    .pitchClassSequence
    .zipWithIndex
    .flatMap { case (pitchClass, step) =>
      Seq(-1, 0, 1).map { accidental => PitchDescriptor(step, pitchClass + accidental) }
    }

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

    def enharmonicEquivalent: Option[PitchDescriptor] = {
      Seq(
        PitchDescriptor((descr.step - 1 + numSteps) % numSteps, descr.pitchClass),
        PitchDescriptor((descr.step + 1) % numSteps, descr.pitchClass)
      ).find { equiv =>
        val pitchClassDifference = Math.abs(tuning.pitchClassSequence(equiv.step) - descr.pitchClass)
        pitchClassDifference == 1 || pitchClassDifference == 11
      }
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

    def asPitchDescriptors: Seq[PitchDescriptor] = {
      scale.pitchClassSequence.zipWithIndex.map { case (pitchClass, step) => PitchDescriptor(step, pitchClass) }
    }
  }
}
