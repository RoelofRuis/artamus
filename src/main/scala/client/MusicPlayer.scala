package client

import music.model.perform.TrackPerformance

// TODO: try to get rid of this..!
trait MusicPlayer {

  def play(notes: TrackPerformance): Unit

}

