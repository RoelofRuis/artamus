package server.actions

import music.model.record.RawMidiNote
import music.primitives.TickResolution
import protocol.v2.Command2

package object recording {

  // Commands
  case class StartRecording(resolution: TickResolution) extends Command2
  case class RecordNote(note: RawMidiNote) extends Command2

}
