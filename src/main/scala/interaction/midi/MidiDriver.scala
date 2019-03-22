package interaction.midi

import application.channels.PlaybackRequest
import application.ports.{Driver, EventBus, MessageBus}
import interaction.midi.device.MidiPlaybackDevice
import javax.inject.Inject

class MidiDriver @Inject() (
  midiPlayback: MidiPlaybackDevice,
  resourceContainer: ResourceContainer
) extends Driver {

  override def close(): Unit = resourceContainer.close()

  override def run(messageBus: MessageBus, eventBus: EventBus): Unit = {
    eventBus.subscribe[PlaybackRequest](request => midiPlayback.playback(request.track))
  }

}
