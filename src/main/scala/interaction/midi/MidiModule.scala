package interaction.midi

import application.ports.{ManagedResource, PlaybackDevice, RecordingDevice}
import com.google.inject.internal.SingletonScope
import interaction.midi.device._
import javax.sound.midi.Sequencer
import net.codingwell.scalaguice.{ScalaMapBinder, ScalaModule, ScalaMultibinder}

class MidiModule extends ScalaModule {

  private val resourceContainer = new ResourceContainer("MIDI")

  override def configure(): Unit = {
    bind[MidiInterface].toProvider[FocusriteMidiInterfaceProvider]

    bind[Sequencer].toProvider[SequencerProvider].in(new SingletonScope())

    ScalaMapBinder.newMapBinder[String, PlaybackDevice](binder)
      .addBinding("midi").to[MidiPlaybackDevice]

    ScalaMapBinder.newMapBinder[String, RecordingDevice](binder)
      .addBinding("midi").to[MidiInputDevice]

    bind[ResourceContainer].toInstance(resourceContainer)
    ScalaMultibinder.newSetBinder[ManagedResource](binder)
      .addBinding.toInstance(resourceContainer)
  }

}
