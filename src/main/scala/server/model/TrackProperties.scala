package server.model

object TrackProperties {
  sealed trait TrackProperty

  case class TimeSignature(num: Int, denom: Int) extends TrackProperty
  case class Key(key: Int) extends TrackProperty

}
