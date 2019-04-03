package application.model

object TrackProperties {
  sealed trait TrackProperty

  case class TicksPerQuarter(ticks: Long) extends TrackProperty

}
