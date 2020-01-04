package midi.v2.impl

import javax.sound.midi.MidiMessage
import midi.v2.api.ReadAction

trait MidiReader {

  def read(pick: List[MidiMessage] => ReadAction): List[MidiMessage]

}
