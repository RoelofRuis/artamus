package server.rendering.service

import java.util.concurrent.{ExecutorService, Executors}

import com.typesafe.scalalogging.LazyLogging
import javax.inject.Inject
import music.domain.write.render.Render
import music.domain.write.track.Track.TrackId
import server.rendering.{LyFile, Renderer, RenderingCompletionHandler}

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
          logger.debug(s"Render for track [$trackId] successful")
          completionHandler.renderingCompleted(Render(trackId, result.file.getAbsolutePath))
        case Success(Left(ex)) => logger.error(s"Render for track [$trackId] failed", ex)
        case Failure(ex) => logger.error(s"Render for track [$trackId] failed", ex)
      }
  }
}
