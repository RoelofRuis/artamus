package interaction.midi.device

import javax.sound.midi.MidiDevice

trait MidiInterface {

  def in: MidiDevice
  def out: MidiDevice

}
