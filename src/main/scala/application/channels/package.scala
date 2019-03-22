package application

import application.model.Track

package object channels {

  trait EventMessage

  case class PlaybackRequest(track: Track) extends EventMessage

}
