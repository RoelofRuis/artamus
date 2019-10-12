package music.primitives

final case class Scale(stepSizes: Seq[Int]) {

  def numberOfSteps: Int = stepSizes.length
  def numberOfPitches: Int = stepSizes.sum
  def pcSequence: Seq[Int] = { stepSizes.indices.map { i => stepSizes.take(i).sum } }

}

object Scale {

  lazy val MAJOR = Scale(Seq(2, 2, 1, 2, 2, 2, 1))
  lazy val MINOR = Scale(Seq(2, 1, 2, 2, 1, 2, 2))

}