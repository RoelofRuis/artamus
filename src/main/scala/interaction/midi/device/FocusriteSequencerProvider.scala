package interaction.midi.device

import com.google.inject.Provider
import javax.sound.midi.{MidiDevice, MidiSystem, Sequencer}

class FocusriteSequencerProvider extends Provider[Sequencer] {

  override def get(): Sequencer = {
    val device: MidiDevice = MidiSystem.getMidiDeviceInfo.find { device =>
    device.getName.contains("Focusrite USB MIDI") && device.getDescription.contains("External MIDI Port")
  }.map(MidiSystem.getMidiDevice).get

    val sequencer: Sequencer = MidiSystem.getSequencer()

    device.open()
    sequencer.open()
    sequencer.getTransmitter.setReceiver(device.getReceiver)

    sequencer
  }

}
