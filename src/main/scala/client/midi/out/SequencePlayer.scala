package client.midi.out

import javax.sound.midi._

class SequencePlayer private[midi] (val device: MidiDevice) {

  private val sequencer: Sequencer = MidiSystem.getSequencer(false)
  private val receiver: Receiver = device.getReceiver

  sequencer.open()
  sequencer.getTransmitter.setReceiver(receiver)

  if (! device.isOpen) device.open()

  def playSequence(sequence: Sequence): Unit = {
    sequencer.setSequence(sequence)
    sequencer.setTempoInBPM(120)

    sequencer.start()

    Thread.sleep(2000) // TODO: improve this sleep (separate thread?)

    sequencer.stop()
  }

  def close(): Unit = {
    sequencer.close()
    receiver.close()
  }

}
