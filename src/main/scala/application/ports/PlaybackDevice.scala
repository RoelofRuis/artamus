package application.ports

import application.model.Unquantized.UnquantizedTrack

trait PlaybackDevice {

  def playbackUnquantized(track: UnquantizedTrack)

}
