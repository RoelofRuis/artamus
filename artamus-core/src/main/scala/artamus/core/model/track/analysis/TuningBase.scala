package artamus.core.model.track.analysis

final case class TuningBase(pcSeq: Seq[Int]) {
  val numSteps: Int = pcSeq.size
  val span: Int = pcSeq.last
  val numPitchClasses: Int = span + 1
}
