package midi.out.impl

import javax.sound.midi.{MetaMessage, MidiDevice, Sequence, Sequencer}
import midi.MidiIO

class DefaultMidiSequencerSink(sequencer: Sequencer) extends MidiSequencerSink {

  private val END_OF_TRACK = 47

  sequencer.addMetaEventListener((meta: MetaMessage) => {
    if (meta.getType == END_OF_TRACK) { sequencer.stop() }
  })

  def writeSequence(sequence: Sequence): MidiIO[Unit] = {
    MidiIO {
      if (sequencer.isRunning) sequencer.stop()
      sequencer.setSequence(sequence)
      sequencer.setTickPosition(0)
      sequencer.setTempoInBPM(120)
      sequencer.start()
    }
  }

}

object DefaultMidiSequencerSink {

  def sequenceToDevice(sequencer: Sequencer, device: MidiDevice): MidiIO[DefaultMidiSequencerSink] = {
    for {
      _ <- MidiIO(sequencer.getTransmitter.setReceiver(device.getReceiver))
    } yield new DefaultMidiSequencerSink(sequencer)
  }

}
