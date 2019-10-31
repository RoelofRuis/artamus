package client.io.midi

import midi.out.{SequenceBuilder, SequenceFormat}
import music.primitives.MidiNoteNumber
import music.symbol.Note
import music.symbol.collection.TrackSymbol

private[midi] case class NoteFormat(symbols: Seq[TrackSymbol[Note]]) extends SequenceFormat {

  val TICKS_PER_WHOLE = 96
  val VOLUME = 32

  import music.analysis.TwelveToneTuning._

  def modify(builder: SequenceBuilder): Unit = {

    builder.setResolution(TICKS_PER_WHOLE / 4)

    symbols.foreach { symbol =>
      val note = symbol.symbol
      // TODO: fix too short durations
      val position = (symbol.position.value * TICKS_PER_WHOLE).n
      val duration = (note.duration.value * TICKS_PER_WHOLE).n
      val pitch = MidiNoteNumber(note.octave,note.pitchClass).value
      builder.addNote(pitch, position, duration, VOLUME)
    }
  }
}
