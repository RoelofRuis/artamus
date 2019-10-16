package server.domain

import music.collection.{SymbolProperties, TrackSymbol}
import music.primitives._
import music.symbols.{Chord, Key, Note, TimeSignature}
import protocol.{Command, Query}

package object track {

  // Commands
  case object NewTrack extends Command
  case class CreateNoteSymbol(position: Position, symbol: SymbolProperties[Note]) extends Command
  case class CreateKeySymbol(position: Position, symbol: SymbolProperties[Key]) extends Command
  case class CreateTimeSignatureSymbol(position: Position, symbol: SymbolProperties[TimeSignature]) extends Command

  // Queries
  case object GetNotes extends Query { type Res = Seq[TrackSymbol[Note]] }
  case object GetChords extends Query { type Res = Seq[TrackSymbol[Chord]] }
  case object GetMidiPitches extends Query { type Res = List[List[Int]] }

}
