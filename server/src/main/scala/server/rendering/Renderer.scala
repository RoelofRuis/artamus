package server.rendering

import nl.roelofruis.artamus.core.api.Event
import nl.roelofruis.artamus.core.model.track.Track
import storage.api.DbIO

trait Renderer {

  def render(track: Track, db: DbIO): List[Event]

}
