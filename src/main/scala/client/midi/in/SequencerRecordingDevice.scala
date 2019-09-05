package client.midi.in

import javax.sound.midi._

/** @deprecated Leaks resources, do not use, keep this implementation only for reference */
class SequencerRecordingDevice private[midi] (val device: MidiDevice, resolution: Int) {

  private val sequencer: Sequencer = MidiSystem.getSequencer(false) // TODO: remove this call!
  private val transmitter: Transmitter = device.getTransmitter

  sequencer.open()
  transmitter.setReceiver(sequencer.getReceiver)

  def readQuarterNotes(): List[Int] = {
    val sequence = new Sequence(Sequence.PPQ, resolution, 1)

    sequencer.setSequence(sequence)
    sequencer.setTempoInBPM(120)
    sequencer.recordEnable(sequence.getTracks()(0), -1)
    sequencer.setTickPosition(0)
    sequencer.startRecording()

    Thread.sleep(5000) // TODO: improve this sleep (separate thread?)

    sequencer.stop()
    sequencer.close()
    transmitter.close()

    val track: Track = sequence.getTracks()(0)

    Range(0, track.size).map { i =>
      track.get(i).getMessage match {
        case msg: ShortMessage if msg.getCommand == ShortMessage.NOTE_ON => Some(msg.getData1)
        case _ => None
      }
    }.collect {
      case Some(i) => i
    }.toList
  }

  def close(): Unit = {
    sequencer.stop()
    sequencer.close()
    device.close()
  }

}
