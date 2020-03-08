package server.rendering

import domain.interact.Event
import domain.write.Track
import storage.api.DbIO

trait Renderer {

  def render(track: Track, db: DbIO): List[Event]

}
