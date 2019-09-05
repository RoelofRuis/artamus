package client.midi.out

import javax.sound.midi._

class SequencePlayer private[midi] (val device: MidiDevice) extends AutoCloseable {

  private val END_OF_TRACK = 47

  private val sequencer: Sequencer = MidiSystem.getSequencer(false)
  private val receiver: Receiver = device.getReceiver

  sequencer.open()
  sequencer.addMetaEventListener((meta: MetaMessage) => {
    if (meta.getType == END_OF_TRACK) {
      sequencer.stop()
    }
  })

  sequencer.getTransmitter.setReceiver(receiver)

  if (! device.isOpen) device.open()

  def playSequence(sequence: Sequence): Unit = {
    if (sequencer.isRunning) sequencer.stop()
    sequencer.setSequence(sequence)
    sequencer.setTickPosition(0)
    sequencer.setTempoInBPM(120)
    sequencer.start()
  }

  def close(): Unit = {
    sequencer.close()
    receiver.close()
  }

}
