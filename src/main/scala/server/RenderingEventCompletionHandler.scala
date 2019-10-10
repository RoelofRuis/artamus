package server

import com.typesafe.scalalogging.LazyLogging
import javax.inject.Inject
import protocol.Event
import pubsub.EventBus
import server.rendering.{RenderingCompleted, RenderingCompletionHandler}

class RenderingEventCompletionHandler @Inject() (
  broadcastEvents: EventBus[Event]
) extends RenderingCompletionHandler with LazyLogging {

  override def renderingCompleted(result: RenderingCompleted): Unit = {
    logger.debug(s"Rendering completed [$result]")
    broadcastEvents.publish(result)
  }

}
