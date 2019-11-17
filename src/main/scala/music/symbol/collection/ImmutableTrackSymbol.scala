package music.symbol.collection

import music.math.temporal.Window
import music.symbol.SymbolType

private[collection] final case class ImmutableTrackSymbol[S <: SymbolType](
  id: Long,
  window: Window,
  symbol: S
) extends TrackSymbol[S] {

  def update(s: S): TrackSymbol[S] = this.copy(symbol = s)

}
