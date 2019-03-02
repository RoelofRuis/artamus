package core.components

import core.musicdata.MusicData

trait PlaybackDevice {

  def play(data: Vector[MusicData])

}
