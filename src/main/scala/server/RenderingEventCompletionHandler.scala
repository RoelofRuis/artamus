package server

import com.typesafe.scalalogging.LazyLogging
import javax.inject.Inject
import music.model.display.render.Render
import protocol.Event
import pubsub.EventBus
import server.domain.writing.TrackRendered
import server.rendering.{RenderingCompletionHandler, RenderingException}
import storage.api.DbWithRead

class RenderingEventCompletionHandler @Inject() (
  broadcastEvents: EventBus[Event],
  db: DbWithRead
) extends RenderingCompletionHandler with LazyLogging {

  import server.model.Renders._

  override def renderingCompleted(result: Either[RenderingException, Render]): Unit = {
    result match {
      case Left(ex) => logger.error(s"Render failed", ex)
      case Right(render) =>
        logger.debug(s"Render for track [${render.trackId}] successful")
        val transaction = db.newTransaction
        transaction.saveRender(render)
        transaction.commit() match {
          case Left(err) => logger.error(s"Unable to commit render results to db", err)
          case Right(_) => broadcastEvents.publish(TrackRendered(render))
        }
    }
  }

}
