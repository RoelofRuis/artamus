package server.domain

import music.math.temporal.{Position, Window}
import music.playback.MidiNote
import music.primitives.{Chord, Key, Note, NoteGroup, TimeSignature}
import protocol.{Command, Query}

package object track {

  // Commands
  case object NewWorkspace extends Command
  case class WriteNoteGroup(group: NoteGroup) extends Command
  case class WriteKey(position: Position, symbol: Key) extends Command
  case class WriteTimeSignature(position: Position, ts: TimeSignature) extends Command

  // Queries
  case object ReadNotes extends Query { type Res = Seq[Note] }
  case object ReadMidiNotes extends Query { type Res = Seq[MidiNote] }
  case object ReadChords extends Query { type Res = Seq[(Window, Chord)] }

}
