package interaction.midi

import application.ports.{ManagedResource, PlaybackDevice}
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

    bind[ResourceContainer].toInstance(resourceContainer)
    ScalaMultibinder.newSetBinder[ManagedResource](binder)
      .addBinding.toInstance(resourceContainer)

    // TODO: use this once the implementation is improved
    //ScalaMapBinder.newMapBinder[String, InputDevice](binder)
    //  .addBinding("midi").to[MidiInputDevice]
  }

}
