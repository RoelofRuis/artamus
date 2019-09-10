package server.domain.track

import javax.inject.Inject
import music.Position
import music.containers.{ImmutableTrack, Track}
import protocol.server.EventBus
import music.properties.Symbols.{StackableSymbol, Symbol}

class TrackState @Inject() (eventBus: EventBus) {

  // TODO: thread safety waarborgen

  private var track: Track = ImmutableTrack.empty

  def setTrackSymbol[A](pos: Position, symbol: A)(implicit ev: Symbol[A]): Unit = {
    track = track.setSymbolAt(pos, symbol)
  }

  def addTrackSymbol[A](pos: Position, symbol: A)(implicit ev: StackableSymbol[A]): Unit = {
    track = track.addSymbolAt(pos, symbol)
    eventBus.publishEvent(TrackSymbolsUpdated)
  }

  def getTrack: Track = track

}
