package midi.v2.out.impl

import javax.sound.midi.Sequence
import midi.v2.MidiIO

trait MidiSequencerSink {

  def writeSequence(sequence: Sequence): MidiIO[Unit]

}
