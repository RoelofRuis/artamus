package server.domain.track

import javax.annotation.concurrent.{GuardedBy, ThreadSafe}
import javax.inject.Inject
import music.domain.track.Track2.TrackId
import music.domain.track.{Track2, TrackRepository}

// TODO: clean up / incorporate into domain
@ThreadSafe
class Savepoint @Inject() (
  repository: TrackRepository
) {

  private val trackLock = new Object()
  @GuardedBy("trackLock") private var editedTrack: Option[TrackId] = None
  @GuardedBy("trackLock") private var stagedTrack: Option[TrackId] = None
  @GuardedBy("trackLock") private var savepoint: Option[TrackId] = None

  def getCurrentTrack: Track2 = trackLock.synchronized {
    editedTrack match {
      case None => Track2()
      case Some(id) => repository.getById(id).get // TODO: remove GET!
    }
  }

  def writeEdit(track: Track2): Unit = trackLock.synchronized {
    editedTrack = Some(repository.write(track))
  }

  def writeStaged(track: Track2): Unit = trackLock.synchronized {
    stagedTrack = Some(repository.write(track))
  }

  def clear(): Unit = trackLock.synchronized {
    savepoint = None
    rollback()
  }

  def commit(): Unit = trackLock.synchronized {
    savepoint = stagedTrack
    editedTrack = stagedTrack
  }

  def rollback(): Unit = trackLock.synchronized {
    editedTrack = savepoint
    stagedTrack = savepoint
  }

}
