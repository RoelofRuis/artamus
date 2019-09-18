package music.symbolic.tuning

import music.symbolic.{Interval, MusicVector, PitchClass, Step}

trait Tuning {

  def stepSizes: Seq[Int]

  def numDistinctSteps: Int

  def numDistinctPitches: Int

  // Do transformations
  def pitchClassToStep(pc: PitchClass): Option[Step]

  def stepToPitchClass(step: Step): PitchClass

  def musicVectorToPitchClass(mvec: MusicVector): PitchClass

  // Math
  // TODO: rewrite these as AbsoluteRelativePair math
  def transpose(i1: Interval, i2: Interval): Interval
  def transpose(mvec: MusicVector, i: Interval): MusicVector
  def addIntervals(i1: Interval, i2: Interval): Interval

  def compare(mvec: MusicVector, pc: PitchClass): Boolean

}
