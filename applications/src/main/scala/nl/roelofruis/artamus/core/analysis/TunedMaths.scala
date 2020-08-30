package nl.roelofruis.artamus.core.analysis

import nl.roelofruis.artamus.core.Pitched._
import nl.roelofruis.artamus.core.analysis.TunedMaths.TuningDefinition

object TunedMaths {

  trait TuningDefinition {
    val pitchClassSequence: List[Int]
    val numPitchClasses: Int
  }

}

trait TunedMaths {
  val tuning: TuningDefinition

  lazy val numSteps: Int = tuning.pitchClassSequence.size

  lazy val getAllPitchDescriptors: Seq[PitchDescriptor] = tuning
    .pitchClassSequence
    .zipWithIndex
    .flatMap { case (pitchClass, step) =>
      Seq(-1, 0, 1).map { accidental => pd(step, pitchClass + accidental) }
    }

  def pd(step: Int, pitchClass: Int): PitchDescriptor = {
    var actualStep = step
    while (actualStep < 0) actualStep += numSteps
    var actualPitchClass = pitchClass
    while (actualPitchClass < 0) actualPitchClass += tuning.numPitchClasses
    PitchDescriptor(actualStep % numSteps, actualPitchClass % tuning.numPitchClasses)
  }

  implicit class PitchDescriptorMath(descr: PitchDescriptor) {
    def +(that: PitchDescriptor): PitchDescriptor = {
      pd(descr.step + that.step, descr.pitchClass + that.pitchClass)
    }

    def -(that: PitchDescriptor): PitchDescriptor = {
      pd(descr.step - that.step, descr.pitchClass - that.pitchClass)
    }

    def enharmonicEquivalent: Option[PitchDescriptor] = {
      Seq(
        pd(descr.step - 1, descr.pitchClass),
        pd(descr.step + 1, descr.pitchClass)
      ).find { equiv =>
        val pitchClassDifference = Math.abs(tuning.pitchClassSequence(equiv.step) - descr.pitchClass)
        pitchClassDifference <= 1 || pitchClassDifference == 11
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
      scale.pitchClassSequence.zipWithIndex.map { case (pitchClass, step) => pd(step, pitchClass) }
    }
  }
}
