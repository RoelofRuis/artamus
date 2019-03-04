package interaction.midi.device

import com.google.inject.{Inject, Provider}
import core.application.ResourceManager
import javax.sound.midi._

class SequencerProvider @Inject() (
  resourceManager: ResourceManager,
  interface: MidiInterface,
) extends Provider[Sequencer] {

  override lazy val get: Sequencer = {
    val sequencer: Sequencer = MidiSystem.getSequencer(false)
    sequencer.open()

    // Hook them up both ways
    sequencer.getTransmitter.setReceiver(interface.out.getReceiver)
    interface.in.getTransmitter.setReceiver(sequencer.getReceiver)

    resourceManager.register("System Sequencer", () => { sequencer.close() })

    sequencer
  }
}
