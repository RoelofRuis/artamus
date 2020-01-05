package midi.v2.out.impl

import javax.sound.midi.{MetaMessage, MidiDevice, Sequence, Sequencer}
import midi.v2.{CommunicationException, InitializationException, MidiIO}

class DefaultMidiSequencerSink(sequencer: Sequencer) extends MidiSequencerSink {

  private val END_OF_TRACK = 47

  sequencer.addMetaEventListener((meta: MetaMessage) => {
    if (meta.getType == END_OF_TRACK) { sequencer.stop() }
  })

  def writeSequence(sequence: Sequence): MidiIO[Unit] = {
    try {
      if (sequencer.isRunning) sequencer.stop()
      sequencer.setSequence(sequence)
      sequencer.setTickPosition(0)
      sequencer.setTempoInBPM(120)
      sequencer.start()
      Right(())
    }catch {
      case ex: Throwable => Left(CommunicationException(ex))
    }
  }

}

object DefaultMidiSequencerSink {

  def sequenceToDevice(sequencer: Sequencer, device: MidiDevice): MidiIO[DefaultMidiSequencerSink] = {
    try {
      sequencer.getTransmitter.setReceiver(device.getReceiver)
      Right(new DefaultMidiSequencerSink(sequencer))
    } catch {
      case ex: Throwable => Left(InitializationException(ex))
    }
  }

}