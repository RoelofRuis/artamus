package music.analysis

final case class TuningSystem(pcSeq: Seq[Int]) {
  val numSteps: Int = pcSeq.size
  val numPitchClasses: Int = pcSeq.last + 1
}
