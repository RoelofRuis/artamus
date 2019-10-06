package server.domain.track

import music.symbolic.temporal.Position
import server.domain.track.container.{SymbolTrack, OrderedSymbolMapBuilder, SymbolProperties}

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

  def readState: SymbolTrack[Position] = mapBuilder.get

}
