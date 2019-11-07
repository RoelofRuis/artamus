package music.symbol.collection

import music.primitives.{Position, Window}
import music.symbol.SymbolType

private[collection] final case class ImmutableTrackSymbol[S <: SymbolType](
  id: Long,
  position: Position,
  symbol: S
) extends TrackSymbol[S] {

  def update(s: S): TrackSymbol[S] = this.copy(symbol = s)
  def window: Window = Window(position, symbol.getDuration)

}
