package server.domain

import music.model.record.RawMidiNote
import music.primitives.{TickPosition, TickResolution}
import protocol.Command

package object recording {

  // Commands
  case class StartRecording(resolution: TickResolution) extends Command
  case class RecordNote(position: TickPosition, note: RawMidiNote) extends Command

}
