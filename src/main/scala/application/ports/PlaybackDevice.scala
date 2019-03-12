package application.ports

import application.model.{Note, Track}

/**
  * A Playback device for playing symbolic music.
  */
trait PlaybackDevice {

  def playback(track: Track[Note])

}
