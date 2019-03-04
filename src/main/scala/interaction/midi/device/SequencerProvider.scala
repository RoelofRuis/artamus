package interaction.midi.device

import com.google.inject.{Inject, Provider}
import core.application.ResourceManager
import javax.sound.midi.{MidiDevice, MidiSystem, Sequencer}

class SequencerProvider @Inject() (
  resourceManager: ResourceManager,
  interface: MidiInterface
) extends Provider[Sequencer] {

  override lazy val get: Sequencer = {
    val sequencer: Sequencer = MidiSystem.getSequencer(false)
    sequencer.open()

    val synth: MidiDevice = MidiSystem.getMidiDeviceInfo
      .find { device => device.getName.contains("Microsoft GS Wavetable Synth") }
      .map(MidiSystem.getMidiDevice).get

    println("Synth: " + synth.getDeviceInfo.getName)
    synth.open()

    // Hook them up both ways
    sequencer.getTransmitter.setReceiver(interface.out.getReceiver)
    interface.in.getTransmitter.setReceiver(sequencer.getReceiver)
    interface.in.getTransmitter.setReceiver(synth.getReceiver)

    resourceManager.register("System Sequencer", () => {
      synth.close()
      sequencer.close()
    })

    sequencer
  }

}
