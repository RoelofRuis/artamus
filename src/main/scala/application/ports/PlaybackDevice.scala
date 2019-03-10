package application.ports

import application.model.Unquantized.UnquantizedTrack

/**
  * A Playback device for playing symbolic music.
  */
trait PlaybackDevice {

  def playbackUnquantized(track: UnquantizedTrack)

}
