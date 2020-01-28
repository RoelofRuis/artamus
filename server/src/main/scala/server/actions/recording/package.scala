package server.actions

import music.model.record.{RawMidiNote, Recording}
import protocol.{Command, Query}

package object recording {

  // Commands
  case class StartRecording() extends Command
  case class StopRecording() extends Command
  case class RecordNote(note: RawMidiNote) extends Command

  // Query
  case object GetCurrentRecording extends Query { type Res = Recording }

}
