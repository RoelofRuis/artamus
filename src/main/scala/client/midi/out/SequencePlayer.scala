package client.midi.out

import javax.sound.midi._

class SequencePlayer private[midi] (val receiver: Receiver, sequencer: Sequencer) {

  private val END_OF_TRACK = 47

  sequencer.open()
  sequencer.addMetaEventListener((meta: MetaMessage) => {
    if (meta.getType == END_OF_TRACK) {
      sequencer.stop()
    }
  })

  sequencer.getTransmitter.setReceiver(receiver)

  def playSequence(sequence: Sequence): Unit = {
    if (sequencer.isRunning) sequencer.stop()
    sequencer.setSequence(sequence)
    sequencer.setTickPosition(0)
    sequencer.setTempoInBPM(120)
    sequencer.start()
  }

}
