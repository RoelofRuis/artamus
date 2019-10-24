package server.domain

import music.symbol.collection.TrackSymbol
import music.primitives._
import music.symbol.{Chord, Key, Note, TimeSignature}
import protocol.{Command, Query}

package object track {

  // Commands
  case object NewTrack extends Command
  case class CreateNoteSymbol(position: Position, symbol: Note) extends Command
  case class CreateKeySymbol(position: Position, symbol: Key) extends Command
  case class CreateTimeSignatureSymbol(position: Position, symbol: TimeSignature) extends Command

  // Queries
  case object GetNotes extends Query { type Res = Seq[TrackSymbol[Note]] }
  case object GetChords extends Query { type Res = Seq[TrackSymbol[Chord]] }
  case object GetMidiPitches extends Query { type Res = List[List[Int]] }

}
