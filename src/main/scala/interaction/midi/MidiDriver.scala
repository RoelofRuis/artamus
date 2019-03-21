package interaction.midi

import application.channels.{Channel, Playback}
import application.ports.{Driver, MessageBus}
import interaction.midi.device.MidiPlaybackDevice
import javax.inject.Inject

class MidiDriver @Inject() (
  midiPlayback: MidiPlaybackDevice,
  playbackChannel: Channel[Playback.type]
) extends Driver {

  override def run(messageBus: MessageBus): Unit = {
    playbackChannel.sub(midiPlayback.playback)
  }

}
