package application.quantization

import application.model.{Ticks, Track}

case class TrackQuantizer() {

  def quantizeTrack[A](track: Track[A]): Track[A] = {
    val onsets: List[Ticks] = track.elements.map { case (timespan, _) => timespan.start }.toList

    val ticksPerQuarter = track.ticksPerQuarter.value / 2

    val spacing = bestSpacing(1, 100, onsets)
    val scaleFactor = ticksPerQuarter / spacing

    // (in.toDouble / cellSize).round * cellSize
    val quantizer: Ticks => Ticks = o => Ticks((o.value.toDouble / ticksPerQuarter).round * ticksPerQuarter)

    track.elements.foreach { case (timespan, _) =>
      val s = timespan.start
      val e = Ticks(timespan.start.value + timespan.duration.value)
      println(s"Quantized [$s - $e] -> [${quantizer(s)} - ${quantizer(e)}]")
    }

    track
  }

  private def bestSpacing(min: Int, max: Int, gVec: Iterable[Ticks]): Int = {

    def error(s: Int): Long = {
      val pointError = gVec.map { g => math.pow(g.value - ((g.value.toDouble / s).round.toInt * s), 2).toInt }.sum
      val gridError = (gVec.head.value + gVec.last.value) / s

      pointError + gridError * 15
    }

    Range.inclusive(min, max).map(i => (i, error(i)))
      .minBy { case (_, err) => err }
      ._1
  }





}
