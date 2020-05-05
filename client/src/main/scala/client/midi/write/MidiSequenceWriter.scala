package client.midi.write

import javax.sound.midi.Sequence
import midi.MidiIO

trait MidiSequenceWriter {

  def writeSequence(sequence: Sequence): MidiIO[Unit]

}
