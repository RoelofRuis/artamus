package client

import music.symbol.Note
import music.symbol.collection.TrackSymbol

trait MusicPlayer {

  def play(notes: Seq[TrackSymbol[Note]]): Unit

}

