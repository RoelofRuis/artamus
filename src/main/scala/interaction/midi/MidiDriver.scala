package interaction.midi

import application.api.{Driver, EventBus, CommandBus}
import application.api.Events.PlaybackRequest
import interaction.midi.device.MidiPlaybackDevice
import javax.inject.Inject

class MidiDriver @Inject() (
  midiPlayback: MidiPlaybackDevice,
  resourceContainer: ResourceContainer
) extends Driver {

  override def close(): Unit = resourceContainer.close()

  override def run(messageBus: CommandBus, eventBus: EventBus): Unit = {
    eventBus.subscribe[PlaybackRequest](request => midiPlayback.playback(request.track))
  }

}
