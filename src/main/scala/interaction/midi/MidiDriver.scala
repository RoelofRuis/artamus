package interaction.midi

import application.channels.PlaybackRequest
import application.ports.{Driver, EventBus, MessageBus}
import interaction.midi.device.MidiPlaybackDevice
import javax.inject.Inject

class MidiDriver @Inject() (midiPlayback: MidiPlaybackDevice) extends Driver {

  override def run(messageBus: MessageBus, eventBus: EventBus): Unit = {
    eventBus.subscribe[PlaybackRequest](request => midiPlayback.playback(request.track))
  }

}
