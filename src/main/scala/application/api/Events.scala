package application.api

import application.domain.Track

object Events {

  trait EventMessage

  // TODO: Separate Track from a `playable` equivalent that is to be broadcasted
  case class PlaybackRequest(track: Track) extends EventMessage

}