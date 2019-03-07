package interaction.midi.device

import com.google.inject.{Inject, Provider}
import application.components.ResourceManager
import javax.sound.midi._

class SequencerProvider @Inject() (
  resourceManager: ResourceManager,
  interfaceProvider: Provider[MidiInterface],
) extends Provider[Sequencer] {

  override lazy val get: Sequencer = {
    val sequencer: Sequencer = MidiSystem.getSequencer(false)
    sequencer.open()

    val interface = interfaceProvider.get
    // Hook them up both ways
    sequencer.getTransmitter.setReceiver(interface.out.getReceiver)
    interface.in.getTransmitter.setReceiver(sequencer.getReceiver)

    resourceManager.register("System Sequencer", () => { sequencer.close() })

    sequencer
  }
}
