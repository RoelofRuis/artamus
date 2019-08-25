package interaction.midi

import application.api.{DevicePool, Driver, RecordingDevice}
import interaction.midi.device._
import net.codingwell.scalaguice.{ScalaMapBinder, ScalaModule}

class MidiModule extends ScalaModule {
  override def configure(): Unit = {
    ScalaMapBinder.newMapBinder[String, Driver](binder)
      .addBinding("midi").to[MidiDriver]

    bind[DevicePool].to[MidiDevicePool]
    bind[MidiDeviceProvider].asEagerSingleton()
//    bind[RecordingDevice].to[MidiInputDevice].asEagerSingleton()
  }

}
