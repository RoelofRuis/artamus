package client

import music.domain.perform.MidiNote

trait MusicPlayer {

  def play(notes: Seq[MidiNote]): Unit

}

