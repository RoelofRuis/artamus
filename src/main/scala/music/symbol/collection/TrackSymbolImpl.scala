package music.symbol.collection

import music.primitives.{Position, Window}
import music.symbol.SymbolType

final case class TrackSymbolImpl[S <: SymbolType](
  id: Long,
  position: Position,
  symbol: S
) extends TrackSymbol[S] {

  def update(s: S): TrackSymbol[S] = this.copy(symbol = s)

  def window: Window = Window(position, Position(position.value + symbol.getDuration.value))

}
