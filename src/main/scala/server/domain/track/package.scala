package server.domain

import music.collection.{SymbolProperties, TrackSymbol}
import music.primitives._
import music.symbols.SymbolType
import protocol.{Command, Query}

package object track {

  // Commands
  case object NewTrack extends Command
  case class CreateSymbol[S <: SymbolType](position: Position, symbol: SymbolProperties[S]) extends Command

  // Queries
  case class GetSymbols[S <: SymbolType]() extends Query { type Res = Seq[TrackSymbol[S]] }
  case object GetMidiPitches extends Query { type Res = List[List[Int]] }

}
