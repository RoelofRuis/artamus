package server.storage

import javax.annotation.concurrent.GuardedBy
import javax.inject.Singleton
import music.domain.track.{Track, TrackRepository}
import music.domain.track.Track.TrackId

import scala.util.{Failure, Success, Try}

@Singleton
class InMemoryTrackRepository() extends TrackRepository {

  private val trackLock = new Object()
  @GuardedBy("trackLock") private var nextId: Long = 0L
  @GuardedBy("trackLock") private var tracks: Map[TrackId, Track] = Map()

  override def create: Try[Track] = trackLock.synchronized {
    val id = TrackId(nextId)
    val trackWithId = Track(id)
    tracks = tracks.updated(id, trackWithId)
    nextId += 1
    Success(trackWithId)
  }

  override def getById(id: TrackId): Try[Track] = trackLock.synchronized {
    tracks.get(id) match {
      case Some(id) => Success(id)
      case None => Failure(EntityNotFoundException("Track"))
    }
  }

  override def put(track: Track): Try[Track] = trackLock.synchronized {
    tracks = tracks.updated(track.id, track)
    Success(track)
  }

}
