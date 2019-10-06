package server.domain.track

import blackboard.{OrderedSymbolMap, OrderedSymbolMapBuilder, TrackSymbol}
import javax.inject.Inject
import music.symbolic.temporal.Position
import pubsub.BufferedEventBus
import server.domain.{DomainEvent, StateChanged}

/* @NotThreadSafe: synchronize access on `track` */
class TrackState @Inject() (domainUpdates: BufferedEventBus[DomainEvent]) {

  private val mapBuilder: OrderedSymbolMapBuilder[Position] = new OrderedSymbolMapBuilder[Position]

  def reset(): Unit = mapBuilder.reset()

  def setSymbol(pos: Position, symbol: TrackSymbol): Unit = {
    mapBuilder.addSymbolAt(pos, symbol)
  }

  def addSymbol(pos: Position, symbol: TrackSymbol): Unit = {
    mapBuilder.addSymbolAt(pos, symbol)
    domainUpdates.publish(StateChanged(mapBuilder.get))
  }

  def readState: OrderedSymbolMap[Position] = mapBuilder.get

}
