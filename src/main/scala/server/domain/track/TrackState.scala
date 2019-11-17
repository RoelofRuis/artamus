package server.domain.track

import javax.annotation.concurrent.{GuardedBy, ThreadSafe}
import music.symbol.collection.Track

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

  def edit(editFunc: Track => Track): Unit = trackLock.synchronized {
    editableTrack = editFunc(editableTrack)
  }

  def getEditable: Track = trackLock.synchronized { editableTrack }

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
