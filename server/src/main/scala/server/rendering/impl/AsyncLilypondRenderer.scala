package server.rendering.impl

import java.util.concurrent.{ExecutorService, Executors}

import domain.interact.Write.TrackRendered
import com.typesafe.scalalogging.LazyLogging
import domain.display.Display
import domain.display.render.Render
import domain.write.Track
import javax.inject.Inject
import server.infra.ServerEventBus
import server.rendering.AsyncRenderer
import storage.api.Database

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

private[rendering] class AsyncLilypondRenderer @Inject() (
  renderingService: LilypondCommandLineExecutor,
  interpreter: LilypondInterpreter,
  eventBus: ServerEventBus,
  db: Database
) extends AsyncRenderer with LazyLogging {

  import server.model.Renders._

  private val executor: ExecutorService = Executors.newFixedThreadPool(1, (r: Runnable) => {
    val t: Thread = Executors.defaultThreadFactory().newThread(r);
    t.setDaemon(true);
    t
  })

  private implicit val executionContext: ExecutionContext = ExecutionContext.fromExecutor(executor)

  override def render(track: Track): Unit = {
    logger.debug(s"Rendering track [${track.id}]")

    Future {
      val displayTrack = Display.displayTrack(track)
      val lyFile = interpreter.interpret(displayTrack)
      renderingService.render(lyFile)
    }.onComplete {
      case Success(Right(result)) => renderingCompleted(Right(Render(track.id, result.file.getAbsolutePath)))
      case Success(Left(ex)) => renderingCompleted(Left(ex))
      case Failure(ex) => renderingCompleted(Left(ex))
    }
  }

  private def renderingCompleted(result: Either[Throwable, Render]): Unit = {
    result match {
      case Left(ex) => logger.error(s"Render failed", ex)
      case Right(render) =>
        logger.debug(s"Render for track [${render.trackId}] successful")
        val transaction = db.newTransaction
        transaction.saveRender(render)
        transaction.commit() match {
          case Left(err) => logger.error(s"Async renderer failed", err)
          case Right(_) => eventBus.publish(TrackRendered(render))
        }
    }
  }


}
