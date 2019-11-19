package music.domain.track

import javax.annotation.concurrent.GuardedBy
import music.domain.track.Track.TrackId

class TrackRepository {

  private val trackLock = new Object()
  @GuardedBy("trackLock") private var nextId: Long = 0L
  @GuardedBy("trackLock") private var tracks: Map[TrackId, Track] = Map()

  def getById(id: TrackId): Option[Track] = tracks.get(id)

  def getNextId: TrackId = trackLock.synchronized {
    val id = nextId
    nextId += 1
    TrackId(id)
  }

  def write(track2: Track): TrackId = trackLock.synchronized {
    track2.id match {
      case None =>
        val id = getNextId
        tracks = tracks.updated(id, track2)
        id
      case Some(id) =>
        tracks = tracks.updated(id, track2)
        id
    }
  }

}
