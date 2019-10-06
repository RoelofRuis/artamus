package server.domain.track

import blackboard.{OrderedSymbolMap, OrderedSymbolMapBuilder, SymbolProperties}
import music.symbolic.temporal.Position

/* @NotThreadSafe: synchronize access on `track` */
class TrackState() {

  private val mapBuilder: OrderedSymbolMapBuilder[Position] = new OrderedSymbolMapBuilder[Position]

  def reset(): Unit = mapBuilder.reset()

  def setSymbol(pos: Position, props: SymbolProperties): Unit = {
    mapBuilder.addSymbolAt(pos, props)
  }

  def addSymbol(pos: Position, props: SymbolProperties): Unit = {
    mapBuilder.addSymbolAt(pos, props)
  }

  def readState: OrderedSymbolMap[Position] = mapBuilder.get

}
