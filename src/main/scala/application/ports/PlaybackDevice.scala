package application.ports

import application.model.Track

/**
  * A Playback device for playing symbolic music.
  */
trait PlaybackDevice {

  def playback(track: Track)

}
