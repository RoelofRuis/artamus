package music.symbolic

final case class Scale(stepSizes: Seq[Int]) {

  def numberOfSteps: Int = stepSizes.length
  def numberOfPitches: Int = stepSizes.sum

}
