package client.io.terminal

import client.MusicPlayer
import music.domain.perform.MidiNote

class TerminalMusicPlayer extends MusicPlayer {

  override def play(notes: Seq[MidiNote]): Unit = {
    println("Playing:")
    notes.foreach { note =>
      println(note)
    }
  }

}
