package server.actions

import music.math.temporal.Position
import music.model.display.render.Render
import music.model.perform.TrackPerformance
import music.primitives._
import protocol.v2.{Command2, Event2, Query2}

package object writing {

  // Commands
  case object NewWorkspace extends Command2
  case class WriteNoteGroup(group: NoteGroup) extends Command2
  case class WriteKey(position: Position, symbol: Key) extends Command2
  case class WriteTimeSignature(position: Position, ts: TimeSignature) extends Command2

  case object Analyse extends Command2
  case object Render extends Command2

  // Queries
  case object Perform extends Query2 { type Res = TrackPerformance }

  // Events
  final case object RenderingStarted extends Event2
  final case class TrackRendered(render: Render) extends Event2

}
