package music.symbolic

object Symbols {

  trait SymbolType

  case object Note extends SymbolType
  case object Chord extends SymbolType
  case object MetaSymbol extends SymbolType


}
