package music.domain.track

import music.domain.track.Track.TrackId

final case class Track(
  id: TrackId
)

object Track {

  final case class TrackId(id: Long) extends AnyVal

}
