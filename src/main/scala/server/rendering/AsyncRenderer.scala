package server.rendering

import java.util.concurrent.{ExecutorService, Executors}

import com.typesafe.scalalogging.LazyLogging
import javax.inject.Inject
import music.model.display.render.Render
import music.model.write.track.Track.TrackId

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

private[rendering] class AsyncRenderer @Inject() (
  renderingService: LilypondCommandLineExecutor,
  completionHandler: RenderingCompletionHandler
) extends Renderer with LazyLogging {

  private val executor: ExecutorService = Executors.newFixedThreadPool(1, (r: Runnable) => {
    val t: Thread = Executors.defaultThreadFactory().newThread(r);
    t.setDaemon(true);
    t
  })

  private implicit val executionContext: ExecutionContext = ExecutionContext.fromExecutor(executor)

  def submit(trackId: TrackId, file: LyFile): Unit = {
    logger.debug(s"Submitting render for track [$trackId]")

    Future(renderingService.render(file))
      .onComplete {
        case Success(Right(result)) =>
          completionHandler.renderingCompleted(Right(Render(trackId, result.file.getAbsolutePath)))
        case Success(Left(ex)) =>
          completionHandler.renderingCompleted(Left(ex))
        case Failure(ex) =>
          completionHandler.renderingCompleted(Left(RenderingException("Async renderer failed", Some(ex))))
      }
  }
}
