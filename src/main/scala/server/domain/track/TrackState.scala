package server.domain.track

import javax.inject.Inject
import music.Position
import music.containers.Track
import protocol.server.EventBus
import music.properties.Symbols.{StackableSymbol, Symbol}

class TrackState @Inject() (eventBus: EventBus) {

  private val track = new Track()

  def setTrackSymbol[A](pos: Position, symbol: A)(implicit ev: Symbol[A]): Unit = {
    track.setSymbol(pos, symbol)
  }

  def addTrackSymbol[A](pos: Position, symbol: A)(implicit ev: StackableSymbol[A]): Unit = {
    track.addSymbol(pos, symbol)
    eventBus.publishEvent(TrackSymbolsUpdated)
  }

  def getTrack: Track = track

}
