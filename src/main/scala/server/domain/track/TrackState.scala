package server.domain.track

import music.symbolic.temporal.Position
import server.domain.track.container.{SymbolProperties, SymbolType, Track}

import scala.reflect.ClassTag

/* @NotThreadSafe: synchronize access on `track` */
class TrackState() {

  private var track: Track = Track.empty

  def reset(): Unit = track = Track.empty

  def addSymbol[S <: SymbolType : ClassTag](pos: Position, props: SymbolProperties): Unit = {
    track = track.upsertSymbolTrack[S](track.getSymbolTrack[S].addSymbolAt(pos, props))
  }

  def readState: Track = track

}
