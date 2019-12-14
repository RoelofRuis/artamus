package music.domain.render

import music.domain.track.Track.TrackId

final case class Render(trackId: TrackId, path: String)
