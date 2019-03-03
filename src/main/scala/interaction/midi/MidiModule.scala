package interaction.midi

import com.google.inject.internal.SingletonScope
import core.components.{InputDevice, PlaybackDevice}
import interaction.midi.device._
import javax.sound.midi.Sequencer
import net.codingwell.scalaguice.ScalaModule

class MidiModule extends ScalaModule {

  override def configure(): Unit = {
    bind[MidiInterface].to[FocusriteMidiInterface].asEagerSingleton()

    bind[Sequencer].toProvider[SequencerProvider].in(new SingletonScope())
    bind[PlaybackDevice].to[MidiPlaybackDevice]
    bind[InputDevice].to[MidiInputDevice]
  }

}
