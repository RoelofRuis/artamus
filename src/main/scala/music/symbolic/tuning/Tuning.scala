package music.symbolic.tuning

import music.symbolic.{MusicVector, PitchClass, Step}

trait Tuning {

  def numDistinctSteps: Int

  def numDistinctPitches: Int

  // Do transformations
  def pitchClassToStep(pc: PitchClass): Option[Step]

  def stepToPitchClass(step: Step): PitchClass

  def musicVectorToPitchClass(mvec: MusicVector): PitchClass

  // Math
  def +(mv1: MusicVector, mv2: MusicVector): MusicVector

  def -(mv1: MusicVector, mv2: MusicVector): MusicVector

}
