package interaction.midi.device

import com.google.inject.name.Named
import com.google.inject.{Inject, Provider}
import core.application.ResourceManager
import javax.sound.midi.{MidiDevice, MidiSystem, Sequencer}

class SequencerProvider @Inject() (
  resourceManager: ResourceManager,
  @Named("midi-interface") device: MidiDevice
) extends Provider[Sequencer] {

  override lazy val get: Sequencer = {
    val sequencer: Sequencer = MidiSystem.getSequencer(false)

    sequencer.open()
    sequencer.getTransmitter.setReceiver(device.getReceiver)

    resourceManager.register("System Sequencer", () => sequencer.close())

    sequencer
  }

}
