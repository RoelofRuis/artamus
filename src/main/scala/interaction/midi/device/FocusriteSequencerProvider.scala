package interaction.midi.device

import com.google.inject.{Inject, Provider}
import core.ResourceManager
import javax.sound.midi.{MidiDevice, MidiSystem, Sequencer}

class FocusriteSequencerProvider @Inject() (resourceManager: ResourceManager) extends Provider[Sequencer] {

  override val get: Sequencer = {
    val device: MidiDevice = MidiSystem.getMidiDeviceInfo.find { device =>
    device.getName.contains("Focusrite USB MIDI") && device.getDescription.contains("External MIDI Port")
  }.map(MidiSystem.getMidiDevice).get

    val sequencer: Sequencer = MidiSystem.getSequencer()

    device.open()
    sequencer.open()
    sequencer.getTransmitter.setReceiver(device.getReceiver)

    resourceManager.registerOnShutdown("Focusrite MIDI", () => sequencer.close())

    sequencer
  }

}
