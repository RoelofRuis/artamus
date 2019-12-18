package server.domain

import music.model.perform.TrackPerformance
import music.primitives._
import music.model.display.render.Render
import music.math.temporal.{Position, Window}
import protocol.{Command, Event, Query}

package object track {

  // Commands
  case object NewWorkspace extends Command
  case class WriteNoteGroup(group: NoteGroup) extends Command
  case class WriteKey(position: Position, symbol: Key) extends Command
  case class WriteTimeSignature(position: Position, ts: TimeSignature) extends Command

  // Queries
  case object LoadRender extends Query { type Res = Option[Render] }
  case object Perform extends Query { type Res = TrackPerformance }

  case object ReadNotes extends Query { type Res = Seq[Note] }
  case object ReadChords extends Query { type Res = Seq[(Window, Chord)] }

  // Events
  final case class TrackRendered(render: Render) extends Event

}
