package interaction.midi

import com.google.inject.internal.SingletonScope
import core.components.{InputDevice, PlaybackDevice}
import interaction.midi.device._
import javax.sound.midi.Sequencer
import net.codingwell.scalaguice.{ScalaMapBinder, ScalaModule}

class MidiModule extends ScalaModule {

  override def configure(): Unit = {
    bind[MidiInterface].to[FocusriteMidiInterface].asEagerSingleton()

    bind[Sequencer].toProvider[SequencerProvider].in(new SingletonScope())

    ScalaMapBinder.newMapBinder[String, PlaybackDevice](binder)
      .addBinding("midi-playbackDevice").to[MidiPlaybackDevice]

    ScalaMapBinder.newMapBinder[String, InputDevice](binder)
      .addBinding("midi-inputDevice").to[MidiInputDevice]
  }

}
