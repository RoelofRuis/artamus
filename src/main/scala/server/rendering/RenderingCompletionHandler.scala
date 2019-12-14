package server.rendering

import music.domain.track.Track.TrackId

trait RenderingCompletionHandler {
  def renderingCompleted(submitter: TrackId, success: Boolean): Unit
}
