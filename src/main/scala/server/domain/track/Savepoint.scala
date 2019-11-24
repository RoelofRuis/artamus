package server.domain.track

import javax.annotation.concurrent.{GuardedBy, ThreadSafe}
import javax.inject.Inject
import music.domain.track.Track.TrackId
import music.domain.track.{Track, TrackRepository}

@Deprecated //Will become workspace in the domain
@ThreadSafe
class Savepoint @Inject() (
  repository: TrackRepository
) {

  private val trackLock = new Object()
  @GuardedBy("trackLock") private var editedTrack: Option[TrackId] = None
  @GuardedBy("trackLock") private var stagedTrack: Option[TrackId] = None
  @GuardedBy("trackLock") private var savepoint: Option[TrackId] = None

  @deprecated
  def writeStaged(track: Track): Unit = trackLock.synchronized {
    stagedTrack = Some(repository.write(track).id.get)
  }

  @deprecated
  def commit(): Unit = trackLock.synchronized {
    savepoint = stagedTrack
    editedTrack = stagedTrack
  }

  @deprecated
  def rollback(): Unit = trackLock.synchronized {
    editedTrack = savepoint
    stagedTrack = savepoint
  }

}
