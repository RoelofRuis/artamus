package server.actions

import music.model.record.RawMidiNote
import protocol.Command

package object recording {

  // Commands
  case class StartRecording() extends Command
  case class StopRecording() extends Command
  case class RecordNote(note: RawMidiNote) extends Command

}
