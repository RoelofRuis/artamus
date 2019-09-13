package server.domain.track

import javax.inject.Inject
import music.Position
import music.containers.{ImmutableTrack, Track}
import music.properties.Symbols.{StackableSymbol, Symbol}
import protocol.Event
import pubsub.EventBus

/* @NotThreadSafe: synchronize acces on `track` */
class TrackState @Inject() (eventBus: EventBus[Event]) {

  private var track: Track = ImmutableTrack.empty

  def setTrackSymbol[A](pos: Position, symbol: A)(implicit ev: Symbol[A]): Unit = {
    track = track.setSymbolAt(pos, symbol)
  }

  def addTrackSymbol[A](pos: Position, symbol: A)(implicit ev: StackableSymbol[A]): Unit = {
    track = track.addSymbolAt(pos, symbol)
    eventBus.publish(TrackSymbolsUpdated)
  }

  def getTrack: Track = track

}
