package midi.out

import javax.sound.midi.{MetaMessage, Receiver, Sequence, Sequencer}

private[midi] class SequenceWriterImpl private[midi] (
  val receiver: Receiver,
  val sequencer: Sequencer
) extends SequenceWriter {

  private val END_OF_TRACK = 47

  sequencer.open()
  sequencer.addMetaEventListener((meta: MetaMessage) => {
    if (meta.getType == END_OF_TRACK) {
      sequencer.stop()
    }
  })

  sequencer.getTransmitter.setReceiver(receiver)

  def writeFromFormat(format: SequenceFormat): Unit = {
    val builder = new SequenceBuilderImpl()
    format.modify(builder)
    writeSequence(builder.build())
  }

  private def writeSequence(sequence: Sequence): Unit = {
    if (sequencer.isRunning) sequencer.stop()
    sequencer.setSequence(sequence)
    sequencer.setTickPosition(0)
    sequencer.setTempoInBPM(120)
    sequencer.start()
  }

}
