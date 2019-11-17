package server.domain.track

import javax.annotation.concurrent.{GuardedBy, ThreadSafe}
import music.math.temporal.{Position, Window}
import music.symbol.{SymbolType, TimeSignature}
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

  // TODO: try to pull editable functions out as (Track => Track)
  def createTimeSignature(position: Position, ts: TimeSignature): Unit = trackLock.synchronized {
    editableTrack = editableTrack.writeTimeSignature(position, ts)
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
