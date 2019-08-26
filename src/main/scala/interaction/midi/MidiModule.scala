package interaction.midi

import application.api.DevicePool
import interaction.midi.device._
import net.codingwell.scalaguice.ScalaModule

class MidiModule extends ScalaModule {
  override def configure(): Unit = {
    bind[DevicePool].to[MidiDevicePool]
    bind[MidiDeviceProvider].asEagerSingleton()
//    bind[RecordingDevice].to[MidiInputDevice].asEagerSingleton()
  }

}
