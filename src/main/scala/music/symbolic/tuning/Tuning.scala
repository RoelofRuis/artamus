package music.symbolic.tuning

import music.symbolic._

trait Tuning {

  def vector(step: Int, accidental: Int): MusicVector

  def stepSizes: Seq[Int]

  def numDistinctSteps: Int

  def numDistinctPitches: Int

  // Do transformations
  def pitchClassToStep(pc: PitchClass): Option[Step]

  def stepToPitchClass(step: Step): PitchClass

  def musicVectorToPitchClass(mvec: MusicVector): PitchClass

  // Math
  // TODO: rewrite these as AbsoluteRelativePair math
  def rectify(mv: MusicVector): MusicVector
  def subtract(mv1: MusicVector, mv2: MusicVector): MusicVector
  def transpose(i1: Interval, i2: Interval): Interval
  def transpose(mvec: MusicVector, i: Interval): MusicVector
  def addIntervals(i1: Interval, i2: Interval): Interval

  def compare(mvec: MusicVector, pc: PitchClass): Boolean

}
