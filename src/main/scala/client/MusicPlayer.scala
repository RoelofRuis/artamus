package client

import music.domain.perform.TrackPerformance

trait MusicPlayer {

  def play(notes: TrackPerformance): Unit

}

