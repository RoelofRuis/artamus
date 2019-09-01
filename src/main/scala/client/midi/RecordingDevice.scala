package client.midi

import javax.sound.midi._

class RecordingDevice private[midi] (val device: MidiDevice) {

  private val sequencer: Sequencer = MidiSystem.getSequencer(false)
  private val transmitter: Transmitter = device.getTransmitter

  private val TICKS_PER_QUARTER = 4 // TODO: pull this out

  sequencer.open()
  transmitter.setReceiver(sequencer.getReceiver)

  if (! device.isOpen) device.open()

  def readQuarterNotes(): List[Int] = {
    val sequence = new Sequence(Sequence.PPQ, TICKS_PER_QUARTER, 1)

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

}
