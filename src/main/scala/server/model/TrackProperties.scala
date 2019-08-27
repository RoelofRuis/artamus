package server.model

import server.util.Rational

object TrackProperties {
  sealed trait TrackProperty

  case class TicksPerQuarter(ticks: Long) extends TrackProperty
  case class TimeSignature(rational: Rational) extends TrackProperty
  case class Key(key: Int) extends TrackProperty

}
