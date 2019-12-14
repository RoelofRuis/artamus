package server.domain

import music.domain.render.Render
import music.math.temporal.{Position, Window}
import music.playback.MidiNote
import music.primitives._
import protocol.{Command, Event, Query}

package object track {

  // Commands
  case object NewWorkspace extends Command
  case class WriteNoteGroup(group: NoteGroup) extends Command
  case class WriteKey(position: Position, symbol: Key) extends Command
  case class WriteTimeSignature(position: Position, ts: TimeSignature) extends Command

  // Queries
  case object LoadRender extends Query { type Res = Option[Render] }
  case object ReadNotes extends Query { type Res = Seq[Note] }
  case object ReadMidiNotes extends Query { type Res = Seq[MidiNote] }
  case object ReadChords extends Query { type Res = Seq[(Window, Chord)] }

  // Events
  final case class TrackRendered(render: Render) extends Event

}
