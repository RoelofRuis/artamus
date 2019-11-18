package music.domain.track

import music.math.temporal.Window
import music.domain.track.symbol.SymbolType

trait TrackSymbol2[S <: SymbolType] {
  val id: Long
  val window: Window
  val symbol: S
  def update(s: S): TrackSymbol2[S]
}

object TrackSymbol2 {

  def apply[S <: SymbolType](id: Long, window: Window, symbol: S): TrackSymbol2[S] = TrackSymbol2Impl(id, window, symbol)

  private[track] final case class TrackSymbol2Impl[S <: SymbolType](
    id: Long,
    window: Window,
    symbol: S
  ) extends TrackSymbol2[S] {

    def update(s: S): TrackSymbol2[S] = this.copy(symbol = s)

  }


}