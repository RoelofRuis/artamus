package music.symbolic

import music.symbolic.Symbols.SymbolType

final case class TrackSymbol[S <: SymbolType](id: Long, props: SymbolProperties[S])
