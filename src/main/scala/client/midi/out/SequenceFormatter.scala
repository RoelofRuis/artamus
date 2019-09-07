package client.midi.out

import javax.sound.midi.{MidiEvent, Sequence, ShortMessage}

// TODO: split into parts, all midi logic should be kept inside midi package.
class SequenceFormatter(resolution: Int) {

  def formatAsQuarterNotes(notes: List[Int]): Sequence = {
    val sequence = new Sequence(Sequence.PPQ, resolution, 1)

    val midiTrack = sequence.createTrack()

    notes
      .zipWithIndex
      .foreach { case (pitch, index) =>
        midiTrack.add(new MidiEvent(new ShortMessage(ShortMessage.NOTE_ON, 0, pitch, 32), index * resolution))
        midiTrack.add(new MidiEvent(new ShortMessage(ShortMessage.NOTE_OFF, 0, pitch, 0), (index + 1) * resolution))
      }

    sequence
  }

}
