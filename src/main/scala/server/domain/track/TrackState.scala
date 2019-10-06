package server.domain.track

import blackboard.{OrderedSymbolMap, TrackSymbol}
import javax.inject.Inject
import music.symbolic.temporal.Position
import pubsub.BufferedEventBus
import server.domain.{DomainEvent, StateChanged}

/* @NotThreadSafe: synchronize acces on `track` */
class TrackState @Inject() (domainUpdates: BufferedEventBus[DomainEvent]) {

  private var symbolMap: OrderedSymbolMap[Position] = OrderedSymbolMap.empty

  def reset(): Unit = symbolMap = OrderedSymbolMap.empty

  def setSymbol(pos: Position, symbol: TrackSymbol): Unit = {
    symbolMap.addSymbolAt(pos, symbol)
  }

  def addSymbol(pos: Position, symbol: TrackSymbol): Unit = {
    symbolMap.addSymbolAt(pos, symbol)
    domainUpdates.publish(StateChanged)
  }

  // TODO: fix state escaping!
  def readState: OrderedSymbolMap[Position] = symbolMap

}
