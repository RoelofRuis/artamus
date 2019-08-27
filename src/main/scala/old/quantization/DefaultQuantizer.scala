package old.quantization

import server.model.SymbolProperties.{NoteDuration, NotePosition, TickDuration, TickPosition}
import server.model.Track
import old.quantization.TrackQuantizer.{End, Params, Quantizer, Start}
import server.util.Rational

case class DefaultQuantizer() extends TrackQuantizer {

  def quantize(track: Track, params: Params): Track = {
    val spacing = detectSpacing(
      params.minGrid,
      params.maxGrid,
      params.gridErrorWeight,
      track.mapSymbolProperties[TickPosition, Long](_.tick)
    )

    val baseNote = Rational(1, 4 * params.ticksPerQuarter)

    val quantizer: Quantizer = {
      case (point, Start) => (point.toDouble / spacing).round
      case (point, End) => (point.toDouble / spacing).round.max(1)
    }

    // TODO: make cleaner separation with build logic
    val qElements = track.mapSymbols { symbol =>
      for {
        tickPos <- symbol.properties.collectFirst { case TickPosition(pos) => pos }
        tickDur <- symbol.properties.collectFirst { case TickDuration(dur) => dur }
      } yield {
        val qStart = quantizer(tickPos, Start)
        val qDur = quantizer(tickPos + tickDur, End) - qStart

        symbol.properties ++ Seq(NotePosition(qStart, baseNote), NoteDuration(qDur, baseNote))
      }
    }

    val builder = Track.builder
    track.getTrackProperties.foreach(builder.addTrackProperty)
    qElements.collect { case Some(props) => props }
      .foreach(builder.addSymbolFromProps)

    builder.build
  }

  private def detectSpacing(min: Int, max: Int, gridErrorWeight: Int, gVec: Iterable[Long]): Int = {

    def error(s: Int): Long = {
      val pointError = gVec.map { g => math.pow(g - ((g.toDouble / s).round.toInt * s), 2).toInt }.sum
      val gridError = (gVec.head + gVec.last) / s

      pointError + (gridError * gridErrorWeight)
    }

    Range.inclusive(min, max)
      .map(i => (i, error(i)))
      .minBy { case (_, err) => err }
      ._1
  }
}