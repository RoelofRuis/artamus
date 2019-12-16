package client.io.midi

import midi.out.{SequenceBuilder, SequenceFormat}
import music.domain.perform.MidiNote

private[midi] case class MidiNoteFormat(symbols: Seq[MidiNote]) extends SequenceFormat {

  val TICKS_PER_WHOLE = 96

  def modify(builder: SequenceBuilder): Unit = {
    builder.setResolution(TICKS_PER_WHOLE / 4)
    symbols.foreach { midiNote =>
      builder.addNote(
        midiNote.noteNumber.value,
        (midiNote.window.start * TICKS_PER_WHOLE).v.n,
        (midiNote.window.duration * TICKS_PER_WHOLE).v.n,
        midiNote.loudness.value
      )
    }
  }
}
