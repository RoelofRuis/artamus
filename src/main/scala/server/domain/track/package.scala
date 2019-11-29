package server.domain

import music.domain.track.TrackSymbol
import music.math.temporal.{Position, Window}
import music.playback.MidiNote
import music.domain.track.symbol.{Chord, Note}
import music.primitives.{Key, TimeSignature}
import protocol.{Command, Query}

package object track {

  // Commands
  case object NewWorkspace extends Command
  case class WriteNote(window: Window, symbol: Note) extends Command
  case class WriteKey(position: Position, symbol: Key) extends Command
  case class WriteTimeSignature(position: Position, ts: TimeSignature) extends Command

  // Queries
  case object ReadNotes extends Query { type Res = Seq[TrackSymbol[Note]] }
  case object ReadMidiNotes extends Query { type Res = Seq[MidiNote] }
  case object ReadChords extends Query { type Res = Seq[TrackSymbol[Chord]] }

}
