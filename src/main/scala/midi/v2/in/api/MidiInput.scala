package midi.v2.in.api

import javax.sound.midi.MidiMessage
import midi.v2.MidiIO
import midi.v2.in.impl.ReadAction

trait MidiInput {

  def readFrom(read: List[MidiMessage] => ReadAction): MidiIO[List[MidiMessage]]

}
