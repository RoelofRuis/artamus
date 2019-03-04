package core.application

import core.components.PlaybackDevice
import core.musicdata.MusicData

class VoidPlaybackDevice extends PlaybackDevice {

  override def play(data: Vector[MusicData]): Unit = ()

}
