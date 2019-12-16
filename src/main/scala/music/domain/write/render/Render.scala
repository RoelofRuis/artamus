package music.domain.write.render

import music.domain.write.track.Track.TrackId

final case class Render(trackId: TrackId, path: String)
