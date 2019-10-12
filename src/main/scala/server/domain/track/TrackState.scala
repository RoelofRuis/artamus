package server.domain.track

import javax.annotation.concurrent.{GuardedBy, ThreadSafe}
import music.primitives.Position
import music.symbols.SymbolType
import music.collection.{SymbolProperties, Track}

import scala.reflect.ClassTag

@ThreadSafe
class TrackState() {

  private val trackLock = new Object()
  @GuardedBy(trackLock) var track: Track = Track.empty

  def newTrack(): Unit = synchronized(trackLock) {
    track = Track.empty
  }

  def createSymbol[S <: SymbolType : ClassTag](pos: Position, props: SymbolProperties[S]): Unit = synchronized(trackLock) {
    track = track.updateSymbolTrack[S](_.addSymbolAt(pos, props))
  }

  def readState: Track = synchronized(trackLock) {
    track
  }

}
