package server.domain

import music.model.record.RawMidiNote
import music.primitives.{TickPosition, TickResolution}

package object recording {

  // Commands
  case class StartRecording(resolution: TickResolution)
  case class RecordNote(position: TickPosition, note: RawMidiNote)

}
