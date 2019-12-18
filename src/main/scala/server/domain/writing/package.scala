package server.domain

import music.math.temporal.Position
import music.model.display.render.Render
import music.model.perform.TrackPerformance
import music.primitives._
import protocol.{Command, Event, Query}

package object writing {

  // Commands
  case object NewWorkspace extends Command
  case class WriteNoteGroup(group: NoteGroup) extends Command
  case class WriteKey(position: Position, symbol: Key) extends Command
  case class WriteTimeSignature(position: Position, ts: TimeSignature) extends Command

  // Queries
  case object Perform extends Query { type Res = TrackPerformance }

  // Events
  final case class TrackRendered(render: Render) extends Event

}
