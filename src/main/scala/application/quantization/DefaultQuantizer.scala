package application.quantization

import application.model.{Ticks, Track}
import application.quantization.DefaultQuantizer.Params

case class DefaultQuantizer() extends TrackSpacingQuantizer {

  def quantize[A](track: Track[A], params: Params): Track[A] = {
    val ticksPerQuarter = track.ticksPerQuarter.value

    val spacing = detectSpacing(
      params.minGrid,
      params.maxGrid,
      params.gridErrorWeight,
      track.onsets
    )

    val quantizer: Ticks => Ticks = { o =>
      Ticks(
        (o.value.toDouble / spacing).round * ticksPerQuarter
      )
    }

    track.quantize(quantizer)
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

  case class Params(
    minGrid: Int,
    maxGrid: Int,
    gridErrorWeight: Int
  )

}
