package server.storage

import javax.annotation.concurrent.GuardedBy
import javax.inject.Singleton
import music.domain.track.{Track, TrackRepository}
import music.domain.track.Track.TrackId

import scala.util.{Success, Try}

@Singleton
class InMemoryTrackRepository() extends TrackRepository {

  private val trackLock = new Object()
  @GuardedBy("trackLock") private var nextId: Long = 0L
  @GuardedBy("trackLock") private var tracks: Map[TrackId, Track] = Map()

  override def getById(id: TrackId): Option[Try[Track]] = trackLock.synchronized { tracks.get(id).map(Success(_)) }

  override def put(track: Track): Try[Track] = trackLock.synchronized {
    track.id match {
      case None =>
        val id = TrackId(nextId)
        val trackWithId = track.setId(id)
        tracks = tracks.updated(id, trackWithId)
        nextId += 1
        Success(trackWithId)
      case Some(id) =>
        tracks = tracks.updated(id, track)
        Success(track)
    }
  }

}
