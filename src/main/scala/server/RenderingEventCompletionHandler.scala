package server

import javax.inject.Inject
import protocol.Event
import pubsub.EventBus
import server.rendering.{RenderingCompleted, RenderingCompletionHandler}

class RenderingEventCompletionHandler @Inject() (
  broadcastEvents: EventBus[Event]
) extends RenderingCompletionHandler {

  override def renderingCompleted(result: RenderingCompleted): Unit = {
    broadcastEvents.publish(result)
  }

}
