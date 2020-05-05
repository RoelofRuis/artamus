package server.rendering

import artamus.core.api.Event
import artamus.core.model.track.Track
import storage.api.DbIO

trait Renderer {

  def render(track: Track, db: DbIO): List[Event]

}
