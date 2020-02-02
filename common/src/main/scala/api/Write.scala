package api

import domain.display.render.Render
import domain.math.temporal.Position
import domain.perform.TrackPerformance
import domain.primitives.{Key, NoteGroup, TimeSignature}

object Write {

  final case object NewWorkspace extends Command
  final case class WriteNoteGroup(group: NoteGroup) extends Command
  final case class WriteKey(position: Position, symbol: Key) extends Command
  final case class WriteTimeSignature(position: Position, ts: TimeSignature) extends Command

  final case object Analyse extends Command
  final case object Render extends Command

  // TODO: rename
  final case object Perform extends Query { type Res = TrackPerformance }

  final case object RenderingStarted extends Event
  final case class TrackRendered(render: Render) extends Event

}
