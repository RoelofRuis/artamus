package application

package object model {

  case class Ticks(value: Long) extends AnyVal

  case class Note(pitch: Int, volume: Int)

  case class TimeSpan(start: Ticks, duration: Ticks) {
    def end: Ticks = Ticks(start.value + duration.value)
  }

  case class Track[A](ticksPerQuarter: Ticks, elements: Iterable[(TimeSpan, A)]) {

    def onsets: Iterable[Ticks] = elements.map { case (timeSpan, _) => timeSpan.start }

    def quantize(targetTicksPerQuarter: Ticks, q: Quantizer): Track[A] = {
      val qElements = elements.map { case (timeSpan, a) =>
        val qStart = q(timeSpan.start, Start)
        val qDur = Ticks(q(timeSpan.end, End).value - qStart.value)
        (TimeSpan(qStart, qDur), a)
      }

      Track(targetTicksPerQuarter, qElements)
    }
  }

  sealed trait TrackType
  case object Unquantized extends TrackType
  case object Quantized extends TrackType

  sealed trait EventBoundary
  case object Start extends EventBoundary
  case object End extends EventBoundary

  type Quantizer = (Ticks, EventBoundary) => Ticks

}
