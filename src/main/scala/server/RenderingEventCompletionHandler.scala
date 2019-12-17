package server

import com.typesafe.scalalogging.LazyLogging
import javax.inject.Inject
import music.domain.write.render.Render
import protocol.Event
import pubsub.EventBus
import server.domain.track.TrackRendered
import server.rendering.RenderingCompletionHandler
import storage.api.DbWithRead

class RenderingEventCompletionHandler @Inject() (
  broadcastEvents: EventBus[Event],
  db: DbWithRead
) extends RenderingCompletionHandler with LazyLogging {

  import server.model.Renders._

  override def renderingCompleted(render: Render): Unit = {
    val transaction = db.newTransaction
    transaction.saveRender(render)
    transaction.commit() match {
      case Left(err) => logger.error(s"Unable to commit render results to db", err)
      case Right(_) => broadcastEvents.publish(TrackRendered(render))
    }
  }

}
