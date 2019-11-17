package music.domain.track

import music.domain.track.Track.TrackId

class TrackRepository {

  private val tracks: Map[TrackId, Track] = Map()

  def getById(id: TrackId): Option[Track] = tracks.get(id)

}
