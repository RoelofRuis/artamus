package client.io.terminal

import client.MusicPlayer
import music.domain.perform.TrackPerformance

class TerminalMusicPlayer extends MusicPlayer {

  override def play(track: TrackPerformance): Unit = {
    println("Playing:")
    track.notes.foreach { note => println(note) }
  }

}
