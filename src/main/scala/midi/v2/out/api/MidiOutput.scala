package midi.v2.out.api

import javax.sound.midi.Sequence
import midi.v2.MidiIO

trait MidiOutput {

  def writeSequence(sequence: Sequence): MidiIO[Unit]

}
