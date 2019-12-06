package music.domain.track

import music.domain.track.Track.TrackId

import scala.util.Try

trait TrackRepository {

  def nextId: Try[TrackId]

  def getById(id: TrackId): Try[Track]

  def put(track: Track): Try[Unit]

}
