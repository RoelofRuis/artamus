package midi.in.impl

import javax.sound.midi.MidiMessage

trait MidiReader {

  def read(pick: List[MidiMessage] => ReadAction): List[MidiMessage]

}
