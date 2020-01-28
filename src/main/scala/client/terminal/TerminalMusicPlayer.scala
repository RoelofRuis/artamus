package client.terminal

import client.MusicPlayer
import javax.inject.Singleton
import music.model.perform.TrackPerformance

@Singleton
class TerminalMusicPlayer extends MusicPlayer {

  override def play(track: TrackPerformance): Unit = {
    println("Playing:")
    track.notes.foreach { note => println(note) }
  }

}
