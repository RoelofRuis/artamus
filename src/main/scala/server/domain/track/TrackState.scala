package server.domain.track

import javax.inject.Inject
import music.symbolic
import music.symbolic.containers.OrderedSymbolMap
import music.symbolic.temporal.Position
import pubsub.BufferedEventBus
import server.domain.{DomainEvent, StateChanged}

/* @NotThreadSafe: synchronize acces on `track` */
class TrackState @Inject() (domainUpdates: BufferedEventBus[DomainEvent]) {

  private var track: OrderedSymbolMap = OrderedSymbolMap.empty

  def reset(): Unit = {
    track = OrderedSymbolMap.empty
  }

  def setTrackSymbol[A : symbolic.Symbol](pos: Position, symbol: A): Unit = {
    track.addSymbolAt(pos, symbol)
  }

  def addTrackSymbol[A : symbolic.Symbol](pos: Position, symbol: A): Unit = {
    track.addSymbolAt(pos, symbol)
    domainUpdates.publish(StateChanged)
  }

  // TODO: leaks the resource, this should be read only!
  def getTrack: OrderedSymbolMap = track

}
