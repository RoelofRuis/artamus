package client.midi.out

import javax.sound.midi.Sequence

trait SequenceWriter {

  def writeSequence(sequence: Sequence): Unit

}
