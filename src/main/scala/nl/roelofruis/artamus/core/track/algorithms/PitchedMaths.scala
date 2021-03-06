package nl.roelofruis.artamus.core.track.algorithms

import nl.roelofruis.artamus.core.track.Pitched._
import nl.roelofruis.artamus.core.track.algorithms.PitchedMaths.TuningDefinition

object PitchedMaths {

  trait TuningDefinition {
    val pitchClassSequence: List[Int]
    val numPitchClasses: Int
  }

}

trait PitchedMaths {
  val settings: TuningDefinition

  lazy val numSteps: Int = settings.pitchClassSequence.size

  lazy val getAllPitchDescriptors: Seq[PitchDescriptor] = settings
    .pitchClassSequence
    .zipWithIndex
    .flatMap { case (pitchClass, step) =>
      Seq(-1, 0, 1).map { accidental => pd(step, pitchClass + accidental) }
    }

  def pdFromAccidental(step: Int, accidental: Int): PitchDescriptor = {
    pd(step, settings.pitchClassSequence(step) + accidental)
  }

  def pd(step: Int, pitchClass: Int): PitchDescriptor = {
    var actualStep = step
    while (actualStep < 0) actualStep += numSteps
    var actualPitchClass = pitchClass
    while (actualPitchClass < 0) actualPitchClass += settings.numPitchClasses
    PitchDescriptor(actualStep % numSteps, actualPitchClass % settings.numPitchClasses)
  }

  implicit class PitchDescriptorMath(descr: PitchDescriptor) {
    def +(that: PitchDescriptor): PitchDescriptor = {
      pd(descr.step + that.step, descr.pitchClass + that.pitchClass)
    }

    def -(that: PitchDescriptor): PitchDescriptor = {
      pd(descr.step - that.step, descr.pitchClass - that.pitchClass)
    }

    val accidentalValue: Int = {
      Seq(
        descr.pitchClass - settings.pitchClassSequence(descr.step),
        (descr.pitchClass - settings.numPitchClasses) - settings.pitchClassSequence(descr.step)
      ).minBy(Math.abs)
    }

    def enharmonicEquivalent: Option[PitchDescriptor] = {
      Seq(
        pd(descr.step - 1, descr.pitchClass),
        pd(descr.step + 1, descr.pitchClass)
      ).find { equiv =>
        val pitchClassDifference = Math.abs(settings.pitchClassSequence(equiv.step) - descr.pitchClass)
        pitchClassDifference <= 1 || pitchClassDifference == 11
      }
    }
  }
}
