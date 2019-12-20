package server.rendering.impl

import java.util.concurrent.{ExecutorService, Executors}

import com.typesafe.scalalogging.LazyLogging
import javax.inject.Inject
import music.model.display.Display
import music.model.display.render.Render
import music.model.write.track.Track
import server.rendering.RenderingCompletionHandler.RenderingException
import server.rendering.{AsyncRenderer, RenderingCompletionHandler}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

private[rendering] class AsyncLilypondRenderer @Inject() (
  renderingService: LilypondCommandLineExecutor,
  interpreter: LilypondInterpreter,
  completionHandler: RenderingCompletionHandler
) extends AsyncRenderer with LazyLogging {

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
      case Success(Right(result)) =>
        completionHandler.renderingCompleted(Right(Render(track.id, result.file.getAbsolutePath)))
      case Success(Left(ex)) =>
        completionHandler.renderingCompleted(Left(ex))
      case Failure(ex) =>
        completionHandler.renderingCompleted(Left(RenderingException("Async renderer failed", Some(ex))))
    }
  }
}
