package midi.out.impl

import javax.sound.midi.Sequence
import midi.MidiIO

trait MidiSequencerSink {

  def writeSequence(sequence: Sequence): MidiIO[Unit]

}
