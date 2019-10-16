package client.io.midi

import midi.out.{SequenceBuilder, SequenceFormat}

private[midi] case class QuarterNotesFormat(notes: List[List[Int]]) extends SequenceFormat {
  def modify(builder: SequenceBuilder): Unit = {
    notes
      .zipWithIndex
      .foreach { case (pitches, index) =>
        pitches.foreach { pitch =>
          builder.addNote(pitch, index, 1, 32)
        }
      }
  }
}
