package server.model

import music.{Key, TimeSignature}

object TrackProperties {
  sealed trait TrackProperty

  // TODO: make it so that objects can be a type of `TrackProperty`
  case class TimeSignatureProp(t: TimeSignature) extends TrackProperty
  case class KeyProp(key: Key) extends TrackProperty

}
