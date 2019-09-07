package client.events

import midi.out.{SequenceBuilder, SequenceFormat}

case class QuarterNotesFormat(notes: List[Int]) extends SequenceFormat {
  def modify(builder: SequenceBuilder): Unit = {
    notes
      .zipWithIndex
      .foreach { case (pitch, index) =>
        builder.addNote(pitch, index, 1, 32)
      }
  }
}
