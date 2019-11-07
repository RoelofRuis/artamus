package client.io.midi

import midi.out.{SequenceBuilder, SequenceFormat}
import music.playback.MidiNote

private[midi] case class MidiNoteFormat(symbols: Seq[MidiNote]) extends SequenceFormat {

  val TICKS_PER_WHOLE = 96

  def modify(builder: SequenceBuilder): Unit = {
    builder.setResolution(TICKS_PER_WHOLE / 4)
    symbols.foreach { midiNote =>
      builder.addNote(
        midiNote.noteNumber.value,
        (midiNote.window.start.value * TICKS_PER_WHOLE).n,
        (midiNote.window.duration.value * TICKS_PER_WHOLE).n,
        midiNote.loudness.value
      )
    }
  }
}
