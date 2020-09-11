package nl.roelofruis.artamus.core.track.analysis

import nl.roelofruis.artamus.core.track.Pitched._
import nl.roelofruis.artamus.core.track.analysis.TunedMaths.TuningDefinition

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
}
