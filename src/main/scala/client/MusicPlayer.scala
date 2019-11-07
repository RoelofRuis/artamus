package client

import music.playback.MidiNote

trait MusicPlayer {

  def play(notes: Seq[MidiNote]): Unit

}

