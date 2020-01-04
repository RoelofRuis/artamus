package midi.v2.api

import javax.sound.midi.MidiMessage

trait MidiInput {

  def readFrom(read: List[MidiMessage] => ReadAction): MidiIO[List[MidiMessage]]

}
