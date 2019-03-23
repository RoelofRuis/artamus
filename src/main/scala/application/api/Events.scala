package application.api

// TODO: Remove references to domain types, use DTO's for communication between layers
import application.domain.Track

object Events {

  trait EventMessage

  case class PlaybackRequest(track: Track) extends EventMessage

}
