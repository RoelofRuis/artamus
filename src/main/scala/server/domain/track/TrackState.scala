package server.domain.track

import javax.inject.Inject
import music.Position
import protocol.server.EventBus
import server.domain.track.Track.{TrackProperty, TrackSymbol}

class TrackState @Inject() (eventBus: EventBus) {

  private val track = Track()

  def addTrackProperty[A](a: A)(implicit ev: TrackProperty[A]): Unit = track.addProperty(a)

  def addTrackSymbol[A](pos: Position, symbol: A)(implicit ev: TrackSymbol[A]): Unit = {
    track.addSymbol(pos, symbol)
    eventBus.publishEvent(TrackSymbolsUpdated)
  }

  def getTrack: Track = track

}
