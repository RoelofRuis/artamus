package application.api

import application.model.event.MidiTrack

object Events {

  trait EventMessage

  // TODO: Separate Track from a `playable` equivalent that is to be broadcasted
  case class PlaybackRequest(track: MidiTrack) extends EventMessage

}
