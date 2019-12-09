package server.storage.memory

import javax.annotation.concurrent.GuardedBy
import javax.inject.Singleton
import music.domain.track.Track.TrackId
import music.domain.track.{Track, TrackRepository}
import server.storage.EntityNotFoundException

import scala.util.{Failure, Success, Try}

@Singleton
class InMemoryTrackRepository() extends TrackRepository {

  private val trackLock = new Object()
  @GuardedBy("trackLock") private var tracks: Map[TrackId, Track] = Map()

  override def removeById(id: TrackId): Try[Unit] = trackLock.synchronized {
    tracks = tracks.removed(id)
    Success(())
  }

  override def getById(id: TrackId): Try[Track] = trackLock.synchronized {
    tracks.get(id) match {
      case Some(id) => Success(id)
      case None => Failure(EntityNotFoundException("Track"))
    }
  }

  override def put(track: Track): Try[Unit] = trackLock.synchronized {
    tracks = tracks.updated(track.id, track)
    Success(())
  }

}
