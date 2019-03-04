package core.application

import core.components.InputDevice
import core.musicdata.MusicData

class VoidInputDevice extends InputDevice {

  override def open: Stream[MusicData] = Stream[MusicData]()

}
