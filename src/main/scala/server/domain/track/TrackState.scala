package server.domain.track

import music.primitives.Position
import music.Symbols.SymbolType
import music.collection.{SymbolProperties, Track}

import scala.reflect.ClassTag

/* @NotThreadSafe: synchronize access on `track` */
class TrackState() {

  private var track: Track = Track.empty

  def reset(): Unit = track = Track.empty

  def addSymbol[S <: SymbolType : ClassTag](pos: Position, props: SymbolProperties[S]): Unit = {
    track = track.updateSymbolTrack[S](_.addSymbolAt(pos, props))
  }

  def readState: Track = track

}
