package server.model

import music.TimeSignature

object TrackProperties {
  sealed trait TrackProperty

  case class TimeSignatureProp(t: TimeSignature) extends TrackProperty // TODO: make it so that time signature can be an type of `TrackProperty`
  case class Key(key: Int) extends TrackProperty

}
