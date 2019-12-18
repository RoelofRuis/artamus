package client

import music.model.perform.TrackPerformance

trait MusicPlayer {

  def play(notes: TrackPerformance): Unit

}

