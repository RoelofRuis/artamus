package midi.in.api

import javax.sound.midi.MidiMessage
import midi.MidiIO
import midi.in.impl.ReadAction

trait MidiInput {

  def readFrom(read: List[MidiMessage] => ReadAction): MidiIO[List[MidiMessage]]

}
