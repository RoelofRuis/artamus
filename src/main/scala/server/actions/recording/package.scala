package server.actions

import music.model.record.RawMidiNote
import music.primitives.TickResolution
import protocol.Command

package object recording {

  // Commands
  case class StartRecording(resolution: TickResolution) extends Command
  case class RecordNote(note: RawMidiNote) extends Command

}
