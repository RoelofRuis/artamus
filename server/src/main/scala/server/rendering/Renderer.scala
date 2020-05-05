package server.rendering

import domain.interact.Event
import nl.roelofruis.artamus.core.model.write.Track
import storage.api.DbIO

trait Renderer {

  def render(track: Track, db: DbIO): List[Event]

}
