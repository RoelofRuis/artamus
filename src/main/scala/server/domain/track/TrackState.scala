package server.domain.track

import javax.inject.Inject
import music.Position
import protocol.server.EventBus
import server.domain.track.Track.TrackSymbol

// TODO: for now only adds events, rethink whether this class is still required
class TrackState @Inject() (eventBus: EventBus) {

  private val track = Track()

  def setTrackSymbol[A](pos: Position, symbol: A)(implicit ev: TrackSymbol[A]): Unit = {
    track.setSymbol(pos, symbol)
  }

  def addTrackSymbol[A](pos: Position, symbol: A)(implicit ev: TrackSymbol[A]): Unit = {
    track.addSymbol(pos, symbol)
    eventBus.publishEvent(TrackSymbolsUpdated) // TODO: should this be put here? How should the event be more specialized?
  }

  def getTrack: Track = track

}
