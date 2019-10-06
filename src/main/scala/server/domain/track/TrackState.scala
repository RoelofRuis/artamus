package server.domain.track

import blackboard.{OrderedSymbolMap, OrderedSymbolMapBuilder, TrackSymbol}
import music.symbolic.temporal.Position

/* @NotThreadSafe: synchronize access on `track` */
class TrackState() {

  private val mapBuilder: OrderedSymbolMapBuilder[Position] = new OrderedSymbolMapBuilder[Position]

  def reset(): Unit = mapBuilder.reset()

  def setSymbol(pos: Position, symbol: TrackSymbol): Unit = {
    mapBuilder.addSymbolAt(pos, symbol)
  }

  def addSymbol(pos: Position, symbol: TrackSymbol): Unit = {
    mapBuilder.addSymbolAt(pos, symbol)
  }

  def readState: OrderedSymbolMap[Position] = mapBuilder.get

}
