package application

import application.model.Track

package object channels {

  trait EventMessage

  case class LoggedMessage(content: String) extends EventMessage
  case class PlaybackRequest(track: Track) extends EventMessage

}
