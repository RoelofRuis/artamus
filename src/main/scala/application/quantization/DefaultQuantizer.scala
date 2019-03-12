package application.quantization

import application.model._
import application.quantization.DefaultQuantizer.Params

case class DefaultQuantizer() extends TrackSpacingQuantizer {

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

object DefaultQuantizer {

  /**
    * @param minGrid            The smallest grid to consider
    * @param maxGrid            The largest grid to consider
    * @param gridErrorWeight    Multiplier which increases the error for many grid lines
    * @param quarterSubdivision The note value to equal whatever spacing the grid picked up
    */
  case class Params(
    minGrid: Int,
    maxGrid: Int,
    gridErrorWeight: Int,
    quarterSubdivision: Int
  )

}
