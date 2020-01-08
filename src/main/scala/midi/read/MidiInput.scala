package midi.read

import javax.sound.midi.MidiMessage
import midi.MidiIO

trait MidiInput {

  def readFrom(read: List[MidiMessage] => ReadAction): MidiIO[List[MidiMessage]]

}
