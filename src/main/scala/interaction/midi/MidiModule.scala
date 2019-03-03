package interaction.midi

import core.components.PlaybackDevice
import interaction.midi.device.{FocusriteSequencerProvider, MidiPlaybackDevice}
import javax.sound.midi.Sequencer
import net.codingwell.scalaguice.ScalaModule

class MidiModule extends ScalaModule {

  override def configure(): Unit = {
    bind[Sequencer].toProvider[FocusriteSequencerProvider]
    bind[PlaybackDevice].to[MidiPlaybackDevice]
  }

}
