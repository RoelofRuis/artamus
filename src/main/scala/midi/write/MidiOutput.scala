package midi.write

import javax.sound.midi.Sequence
import midi.MidiIO

trait MidiOutput {

  def writeSequence(sequence: Sequence): MidiIO[Unit]

}
