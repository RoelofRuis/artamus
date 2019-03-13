package application.quantization

import application.model._
import application.quantization.TrackQuantizer.Params

case class DefaultQuantizer() extends TrackQuantizer {

  def quantize[A](track: Track[A], params: Params): Track[A] = {
    val spacing = detectSpacing(
      params.minGrid,
      params.maxGrid,
      params.gridErrorWeight,
      track.onsets
    )

    val quantizer: Quantizer = {
      case (point, Start) => Ticks((point.value.toDouble / spacing).round)
      case (point, End) => Ticks((point.value.toDouble / spacing).round.max(1))
    }

    track.quantize(Ticks(params.quarterSubdivision), quantizer)
  }

  private def detectSpacing(min: Int, max: Int, gridErrorWeight: Int, gVec: Iterable[Ticks]): Int = {

    def error(s: Int): Long = {
      val pointError = gVec.map { g => math.pow(g.value - ((g.value.toDouble / s).round.toInt * s), 2).toInt }.sum
      val gridError = (gVec.head.value + gVec.last.value) / s

      pointError + (gridError * gridErrorWeight)
    }

    Range.inclusive(min, max)
      .map(i => (i, error(i)))
      .minBy { case (_, err) => err }
      ._1
  }
}
