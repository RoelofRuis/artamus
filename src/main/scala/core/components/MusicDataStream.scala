package core.components

import core.musicdata.MusicData

trait MusicDataStream {

  def open: Stream[MusicData]

}
