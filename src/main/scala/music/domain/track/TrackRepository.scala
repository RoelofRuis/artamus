package music.domain.track

import music.domain.track.Track.TrackId

import scala.util.Try

trait TrackRepository {

  def getById(id: TrackId): Option[Try[Track]]

  def put(track: Track): Try[Track]

}
