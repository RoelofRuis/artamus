package server.domain.track

import javax.inject.Inject
import music.Position
import protocol.server.EventBus
import server.domain.track.Track.{StackableTrackSymbol, TrackSymbol}

class TrackState @Inject() (eventBus: EventBus) {

  private val track = new Track()

  def setTrackSymbol[A](pos: Position, symbol: A)(implicit ev: TrackSymbol[A]): Unit = {
    track.setSymbol(pos, symbol)
  }

  def addTrackSymbol[A](pos: Position, symbol: A)(implicit ev: StackableTrackSymbol[A]): Unit = {
    track.addSymbol(pos, symbol)
    eventBus.publishEvent(TrackSymbolsUpdated)
  }

  def getTrack: Track = track

}
