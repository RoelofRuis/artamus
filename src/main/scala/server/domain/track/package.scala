package server.domain

import music.collection.{SymbolProperties, TrackSymbol}
import music.primitives._
import music.symbols.{Chord, MetaSymbol, Note}
import protocol.{Command, Query}

package object track {

  // Commands
  case object NewTrack extends Command
  case class CreateNoteSymbol(position: Position, symbol: SymbolProperties[Note.type]) extends Command
  case class CreateMetaSymbol(position: Position, symbol: SymbolProperties[MetaSymbol.type]) extends Command

  // Queries
  case object GetNotes extends Query { type Res = Seq[TrackSymbol[Note.type]] }
  case object GetChords extends Query { type Res = Seq[TrackSymbol[Chord.type]] }
  case object GetMidiPitches extends Query { type Res = List[List[Int]] }

}
