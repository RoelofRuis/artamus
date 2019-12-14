package server

import com.typesafe.scalalogging.LazyLogging
import javax.inject.Inject
import music.domain.render.Render
import music.domain.track.Track.TrackId
import protocol.Event
import pubsub.EventBus
import server.domain.RenderingCompleted
import server.rendering.{Renderer, RenderingCompletionHandler}
import server.storage.FileDb

class RenderingEventCompletionHandler @Inject() (
  renderer: Renderer,
  broadcastEvents: EventBus[Event],
  db: FileDb
) extends RenderingCompletionHandler with LazyLogging {

  import server.storage.entity.Renders._

  override def renderingCompleted(submitter: TrackId, success: Boolean): Unit = {
    if (success) {
      logger.debug(s"Rendering successful")
      renderer.getRender(submitter) match {
        case Some(file) =>
          val transaction = db.newTransaction
          transaction.saveRender(Render(submitter, file.getAbsolutePath))
          transaction.commit() match {
            case Left(err) => logger.error(s"Unable to commit render results to db", err)
            case Right(_) => broadcastEvents.publish(RenderingCompleted(file))
          }
        case None => logger.error(s"No rendering for [$submitter] while completed successfully!")
      }
    } else {
      logger.debug(s"Rendering failed")
    }
  }

}
