package server.domain.track

import javax.inject.Inject
import music.symbolic.containers.TrackSymbol
import music.symbolic.containers.{OrderedSymbolMap, ReadableSymbolMap}
import music.symbolic.temporal.Position
import pubsub.BufferedEventBus
import server.domain.{DomainEvent, StateChanged}

/* @NotThreadSafe: synchronize acces on `track` */
class TrackState @Inject() (domainUpdates: BufferedEventBus[DomainEvent]) {

  private var symbolMap: OrderedSymbolMap = OrderedSymbolMap.empty

  def reset(): Unit = symbolMap = OrderedSymbolMap.empty

  def setSymbol(pos: Position, symbol: TrackSymbol): Unit = {
    symbolMap.addSymbolAt(pos, symbol)
  }

  def addSymbol(pos: Position, symbol: TrackSymbol): Unit = {
    symbolMap.addSymbolAt(pos, symbol)
    domainUpdates.publish(StateChanged)
  }

  // TODO: see if state is sufficiently protected this way
  def readState: ReadableSymbolMap = symbolMap

}
