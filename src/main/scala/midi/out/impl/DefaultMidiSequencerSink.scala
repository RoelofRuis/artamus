package midi.out.impl

import javax.sound.midi.{MetaMessage, MidiDevice, Sequence, Sequencer}
import midi.MidiIO

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
      MidiIO.ok
    }catch {
      case ex: Throwable => MidiIO.communicationException(ex)
    }
  }

}

object DefaultMidiSequencerSink {

  def sequenceToDevice(sequencer: Sequencer, device: MidiDevice): MidiIO[DefaultMidiSequencerSink] = {
    try {
      sequencer.getTransmitter.setReceiver(device.getReceiver)
      MidiIO.of(new DefaultMidiSequencerSink(sequencer))
    } catch {
      case ex: Throwable => MidiIO.unableToInitialize(ex)
    }
  }

}
