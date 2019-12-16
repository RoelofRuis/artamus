package server

import com.typesafe.scalalogging.LazyLogging
import javax.inject.Inject
import music.domain.render.Render
import music.domain.track.Track.TrackId
import protocol.Event
import pubsub.EventBus
import server.domain.track.TrackRendered
import server.rendering.{Renderer, RenderingCompletionHandler}
import storage.api.DbWithRead

class RenderingEventCompletionHandler @Inject() (
  renderer: Renderer,
  broadcastEvents: EventBus[Event],
  db: DbWithRead
) extends RenderingCompletionHandler with LazyLogging {

  import server.storage.Renders._

  override def renderingCompleted(submitter: TrackId, success: Boolean): Unit = {
    if (success) {
      logger.debug(s"Rendering successful")
      renderer.getRender(submitter) match {
        case Some(file) =>
          val transaction = db.newTransaction
          val render = Render(submitter, file.getAbsolutePath)
          transaction.saveRender(render)
          transaction.commit() match {
            case Left(err) => logger.error(s"Unable to commit render results to db", err)
            case Right(_) => broadcastEvents.publish(TrackRendered(render))
          }
        case None => logger.error(s"No rendering for [$submitter] while completed successfully!")
      }
    } else {
      logger.debug(s"Rendering failed")
    }
  }

}
