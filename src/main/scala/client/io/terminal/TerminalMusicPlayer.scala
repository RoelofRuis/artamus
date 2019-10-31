package client.io.terminal

import client.MusicPlayer
import music.symbol.Note
import music.symbol.collection.TrackSymbol

class TerminalMusicPlayer extends MusicPlayer {

  override def play(notes: Seq[TrackSymbol[Note]]): Unit = {
    println("Playing:")
    notes.foreach { note =>
      println(note)
    }
  }

}
