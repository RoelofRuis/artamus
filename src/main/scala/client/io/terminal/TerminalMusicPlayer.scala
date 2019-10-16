package client.io.terminal

import client.MusicPlayer

class TerminalMusicPlayer extends MusicPlayer {

  override def play(notes: List[List[Int]]): Unit = {
    println("Playing:")
    notes.foreach { chord =>
      println(chord.mkString("<", " ", ">"))
    }
  }

}
