package interaction.midi

import com.google.inject.internal.SingletonScope
import application.components.PlaybackDevice
import interaction.midi.device._
import javax.sound.midi.Sequencer
import net.codingwell.scalaguice.{ScalaMapBinder, ScalaModule}

class MidiModule extends ScalaModule {

  override def configure(): Unit = {
    bind[MidiInterface].toProvider[FocusriteMidiInterfaceProvider]

    bind[Sequencer].toProvider[SequencerProvider].in(new SingletonScope())

    ScalaMapBinder.newMapBinder[String, PlaybackDevice](binder)
      .addBinding("midi").to[MidiPlaybackDevice]

    // TODO: use this once the implementation is improved
    //ScalaMapBinder.newMapBinder[String, InputDevice](binder)
    //  .addBinding("midi").to[MidiInputDevice]
  }

}
