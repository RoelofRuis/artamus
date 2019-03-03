package interaction.midi

import core.components.PlaybackDevice
import interaction.midi.device.{FocusriteDeviceProvider, MidiPlaybackDevice, SequencerProvider}
import javax.sound.midi.{MidiDevice, Sequencer}
import net.codingwell.scalaguice.ScalaModule

class MidiModule extends ScalaModule {

  override def configure(): Unit = {
    bind[MidiDevice].annotatedWithName("midi-interface").toProvider[FocusriteDeviceProvider]

    bind[Sequencer].toProvider[SequencerProvider]
    bind[PlaybackDevice].to[MidiPlaybackDevice]
  }

}
