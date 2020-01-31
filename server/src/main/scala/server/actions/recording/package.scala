package server.actions

import music.model.record.{RawMidiNote, Recording}
import protocol.{Command, Query}

package object recording {

  // Commands
  case class ClearRecording() extends Command
  case class RecordNote(note: RawMidiNote) extends Command
  case class Quantize(wholeNoteDuration: Option[Int]) extends Command

  // Query
  case object GetCurrentRecording extends Query { type Res = Recording }

}
