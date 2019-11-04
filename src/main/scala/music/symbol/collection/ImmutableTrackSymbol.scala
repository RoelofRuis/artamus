package music.symbol.collection

import music.primitives.{Duration, Position, Window}
import music.symbol.SymbolType

private[collection] final case class ImmutableTrackSymbol[S <: SymbolType](
  id: Long,
  position: Position,
  symbol: S
) extends TrackSymbol[S] {

  def update(s: S): TrackSymbol[S] = this.copy(symbol = s)

  def duration: Duration = symbol.getDuration

  def window: Window = Window.between(position, Position(position.value + duration.value))

}
