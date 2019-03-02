package core.components

import core.musicdata.MusicData

trait InputDevice {

  def open: Stream[MusicData]

}
