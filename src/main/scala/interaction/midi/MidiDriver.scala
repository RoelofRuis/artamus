package interaction.midi

import application.api.Events.PlaybackRequest
import application.api.{CommandBus, Driver, EventBus}
import interaction.midi.device.{MidiDeviceProvider, MidiPlaybackDevice}
import javax.inject.Inject

class MidiDriver @Inject() (
  midiPlayback: MidiPlaybackDevice,
  midiDevicePool: MidiDeviceProvider
) extends Driver {

  override def close(): Unit = midiDevicePool.close()

  override def run(messageBus: CommandBus, eventBus: EventBus): Unit = {
    eventBus.subscribe[PlaybackRequest](request => midiPlayback.playback(request.track))
  }

}
