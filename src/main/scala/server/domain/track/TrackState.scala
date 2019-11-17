package server.domain.track

import javax.annotation.concurrent.{GuardedBy, ThreadSafe}
import music.math.temporal.Window
import music.symbol.SymbolType
import music.symbol.collection.Track

import scala.reflect.ClassTag

@ThreadSafe
class TrackState() {

  // TODO: rethink this as a state machine and clean up
  // TrackState should only reference track ID

  private val trackLock = new Object()
  @GuardedBy("trackLock") private var stagedTrack: Track = Track.empty
  @GuardedBy("trackLock") private var savepoint: Track = Track.empty
  @GuardedBy("trackLock") private var editableTrack: Track = Track.empty

  def clear(): Unit = trackLock.synchronized {
    savepoint = Track.empty
    rollback()
  }

  def createSymbol[S <: SymbolType: ClassTag](window: Window, props: S): Unit = trackLock.synchronized {
    editableTrack = editableTrack.create(window, props)
  }

  def getEditable: Track = trackLock.synchronized { editableTrack }

  def getStaged: Track = trackLock.synchronized { stagedTrack }

  def stage(track: Track): Unit = trackLock.synchronized { stagedTrack = track }

  def commit(): Unit = trackLock.synchronized {
    savepoint = stagedTrack
    editableTrack = stagedTrack
  }

  def rollback(): Unit = trackLock.synchronized {
    editableTrack = savepoint
    stagedTrack = savepoint
  }

}
