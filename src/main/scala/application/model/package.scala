package application

package object model {

  case class Ticks(value: Long) extends AnyVal

  case class Note(pitch: Int, volume: Int)

  case class TimeSpan(start: Ticks, duration: Ticks) {
    def end: Ticks = Ticks(start.value + duration.value)
  }

  case class Track[A](ticksPerQuarter: Ticks, elements: Iterable[(TimeSpan, A)]) {

    def onsets: Iterable[Ticks] = elements.map { case (timeSpan, _) => timeSpan.start }

    def quantize(f: Ticks => Ticks): Track[A] = {
      val qElements = elements.map { case (timeSpan, a) =>
        val qStart = f(timeSpan.start)
        val qDur = Ticks(f(timeSpan.end).value - qStart.value)
        (TimeSpan(qStart, qDur), a)
      }

      Track(ticksPerQuarter, qElements)
    }
  }

}
