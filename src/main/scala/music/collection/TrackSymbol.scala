package music.collection

import music.Symbols.SymbolType

final case class TrackSymbol[S <: SymbolType](id: Long, props: SymbolProperties[S])
