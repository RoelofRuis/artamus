package music.domain.track

import music.math.temporal.Window
import music.domain.track.symbol.SymbolType

trait TrackSymbol[S <: SymbolType] {
  val id: Long
  val window: Window
  val symbol: S
  def update(s: S): TrackSymbol[S]
}

object TrackSymbol {

  def apply[S <: SymbolType](id: Long, window: Window, symbol: S): TrackSymbol[S] = TrackSymbolImpl(id, window, symbol)

  private[track] final case class TrackSymbolImpl[S <: SymbolType](
    id: Long,
    window: Window,
    symbol: S
  ) extends TrackSymbol[S] {

    def update(s: S): TrackSymbol[S] = this.copy(symbol = s)

  }


}