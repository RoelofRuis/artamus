package interaction.midi

import application.api.{Driver, RecordingDevice}
import com.google.inject.internal.SingletonScope
import interaction.midi.device._
import javax.sound.midi.Sequencer
import net.codingwell.scalaguice.{ScalaMapBinder, ScalaModule}

class MidiModule extends ScalaModule {

  override def configure(): Unit = {
    bind[MidiInterface].toProvider[FocusriteMidiInterfaceProvider]

    ScalaMapBinder.newMapBinder[String, Driver](binder)
      .addBinding("midi").to[MidiDriver]

    bind[Sequencer].toProvider[SequencerProvider].in(new SingletonScope())
    bind[RecordingDevice].to[MidiInputDevice]

    bind[ResourceContainer].toInstance(new ResourceContainer("MIDI"))
  }

}
