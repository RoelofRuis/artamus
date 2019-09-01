package client.midi

import javax.sound.midi._

class PlaybackDevice private[midi] (val device: MidiDevice, resolution: Int) {

  private val sequencer: Sequencer = MidiSystem.getSequencer(false)
  private val receiver: Receiver = device.getReceiver

  sequencer.open()
  sequencer.getTransmitter.setReceiver(receiver)

  if (! device.isOpen) device.open()

  def sendQuarterNotes(notes: List[Int]): Unit = {
    val sequence = new Sequence(Sequence.PPQ, resolution, 1)

    val midiTrack = sequence.createTrack()

    notes
      .zipWithIndex
      .foreach { case (pitch, index) =>
        midiTrack.add(new MidiEvent(new ShortMessage(ShortMessage.NOTE_ON, 0, pitch, 32), index * resolution))
        midiTrack.add(new MidiEvent(new ShortMessage(ShortMessage.NOTE_OFF, 0, pitch, 0), (index + 1) * resolution))
      }

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
