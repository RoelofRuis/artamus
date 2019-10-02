package music.symbolic.pitch

final case class Scale(stepSizes: Seq[Int]) {

  def numberOfSteps: Int = stepSizes.length
  def numberOfPitches: Int = stepSizes.sum

}

object Scale {

  lazy val MAJOR = Scale(Seq(2, 2, 1, 2, 2, 2, 1))
  lazy val MINOR = Scale(Seq(2, 1, 2, 2, 1, 2, 2))

}