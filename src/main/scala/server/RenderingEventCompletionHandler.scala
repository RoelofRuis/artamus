package server

import com.typesafe.scalalogging.LazyLogging
import javax.inject.Inject
import protocol.Event
import pubsub.EventBus
import server.control.RenderingCompleted
import server.rendering.{Renderer, RenderingCompletionHandler}

class RenderingEventCompletionHandler @Inject() (
  renderer: Renderer,
  broadcastEvents: EventBus[Event]
) extends RenderingCompletionHandler with LazyLogging {

  override def renderingCompleted(submitter: String, success: Boolean): Unit = {
    if (success) {
      logger.debug(s"Rendering successful")
      renderer.getRender(submitter) match {
        case Some(file) => broadcastEvents.publish(RenderingCompleted(file))
        case None => logger.error(s"No rendering for [$submitter] while completed successfully!")
      }
    } else {
      logger.debug(s"Rendering failed")
    }
  }

}
