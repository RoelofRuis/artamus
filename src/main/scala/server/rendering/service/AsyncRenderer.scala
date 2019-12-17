package server.rendering.service

import com.typesafe.scalalogging.LazyLogging
import javax.inject.Inject
import music.domain.write.render.Render
import music.domain.write.track.Track.TrackId
import server.rendering.{LyFile, Renderer, RenderingCompletionHandler}

import scala.util.{Failure, Success}

private[rendering] class AsyncRenderer @Inject() (
  renderingService: LilypondCommandLineExecutor,
  completionHandler: RenderingCompletionHandler
) extends Renderer with LazyLogging {

  def submit(trackId: TrackId, file: LyFile): Unit = {
    logger.debug(s"Submitting render for track [$trackId]")
    renderingService.render(file, {
      case Success(result: RenderingResult) =>
        logger.debug(s"Render for track [$trackId] successful")
        completionHandler.renderingCompleted(Render(trackId, result.file.getAbsolutePath))

      case Failure(ex) => logger.error(s"Render for track [$trackId] failed", ex)
    })
  }
}
