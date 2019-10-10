package music.collection

import music.symbols.SymbolType

final case class TrackSymbol[S <: SymbolType](id: Long, props: SymbolProperties[S])
