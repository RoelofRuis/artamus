package music.domain.track

import javax.annotation.concurrent.GuardedBy
import javax.inject.Singleton
import music.domain.track.Track.TrackId

@Singleton
class TrackRepository {

  private val trackLock = new Object()
  @GuardedBy("trackLock") private var nextId: Long = 0L
  @GuardedBy("trackLock") private var tracks: Map[TrackId, Track] = Map()

  def getById(id: TrackId): Option[Track] = trackLock.synchronized { tracks.get(id) }

  def write(track: Track): Track = trackLock.synchronized {
    track.id match {
      case None =>
        val id = TrackId(nextId)
        val trackWithId = track.setId(id)
        tracks = tracks.updated(id, trackWithId)
        nextId += 1
        trackWithId
      case Some(id) =>
        tracks = tracks.updated(id, track)
        track
    }
  }

}
