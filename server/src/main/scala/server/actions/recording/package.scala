package server.actions

import music.model.record.{Quantizer, RawMidiNote, Recording}
import protocol.{Command, Query}

package object recording {

  // Commands
  case class ClearRecording() extends Command
  case class RecordNote(note: RawMidiNote) extends Command
  case class Quantize(customQuantizer: Option[Quantizer], rhythmOnly: Boolean) extends Command

  // Query
  case object GetCurrentRecording extends Query { type Res = Recording }

}
