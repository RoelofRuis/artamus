package midi.out

import javax.sound.midi.{MidiEvent, Sequence, ShortMessage}

private[midi] class SimpleSequenceBuilder private[midi] (resolution: Int) extends SequenceBuilder {

  private val sequence = new Sequence(Sequence.PPQ, resolution, 1)
  private val midiTrack = sequence.createTrack()

  def addNote(pitch: Int, start: Int, duration: Int, volume: Int): Unit = {
    midiTrack.add(new MidiEvent(new ShortMessage(ShortMessage.NOTE_ON, 0, pitch, volume), start * resolution))
    midiTrack.add(new MidiEvent(new ShortMessage(ShortMessage.NOTE_OFF, 0, pitch, 0), (start + duration) * resolution))
  }

  def build(): Sequence = sequence

}
