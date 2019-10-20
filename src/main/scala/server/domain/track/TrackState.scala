package server.domain.track

import javax.annotation.concurrent.{GuardedBy, ThreadSafe}
import music.collection.Track
import music.primitives.Position
import music.symbols.SymbolType

import scala.reflect.ClassTag

@ThreadSafe
class TrackState() {

  private val trackLock = new Object()
  @GuardedBy("trackLock") var track: Track = Track.empty

  def newTrack(): Unit = trackLock.synchronized {
    track = Track.empty
  }

  def createSymbol[S <: SymbolType: ClassTag](pos: Position, props: S): Unit = trackLock.synchronized {
    track = track.updateSymbolTrack[S](_.addSymbolAt(pos, props))
  }

  def readState: Track = trackLock.synchronized {
    track
  }

}
