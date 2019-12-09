package music.domain.track

import music.domain.track.Track.TrackId

import scala.util.Try

trait TrackRepository {

  def getById(id: TrackId): Try[Track]

  def removeById(id: TrackId): Try[Unit]

  def put(track: Track): Try[Unit]

}
