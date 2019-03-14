package application.model

import application.model.Track.{End, Quantizer, Start}

case class Track(ticksPerQuarter: Ticks, elements: Iterable[(TimeSpan, Note)]) {

  def onsets: Iterable[Ticks] = elements.map { case (timeSpan, _) => timeSpan.start }

  // TODO: Might not belong here, how to construct new ID?
  def quantize(targetTicksPerQuarter: Ticks, q: Quantizer): Track = {
    val qElements = elements.map { case (timeSpan, a) =>
      val qStart = q(timeSpan.start, Start)
      val qDur = Ticks(q(timeSpan.end, End).value - qStart.value)
      (TimeSpan(qStart, qDur), a)
    }

    Track(targetTicksPerQuarter, qElements)
  }
}

object Track {

  // Might be solved more elegantly using scalaz or other type library
  sealed trait TrackType
  case object Unquantized extends TrackType
  case object Quantized extends TrackType

  sealed trait EventBoundary
  case object Start extends EventBoundary
  case object End extends EventBoundary

  type Quantizer = (Ticks, EventBoundary) => Ticks

}
