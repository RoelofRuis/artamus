package server.rendering.service

import java.io.File

import com.typesafe.scalalogging.LazyLogging
import javax.annotation.concurrent.NotThreadSafe
import javax.inject.Inject
import music.domain.write.track.Track.TrackId
import server.rendering.{LyFile, Renderer, RenderingCompletionHandler}

@NotThreadSafe
private[rendering] class AsyncRenderer @Inject() (
  renderingService: LilypondCommandLineExecutor,
  completionHandler: RenderingCompletionHandler
) extends Renderer with LazyLogging {

  private var rendersInProgress = Map[Long, TrackId]()
  private var rendersCompleted = Map[TrackId, File]()

  renderingService.setCompletionCallback(complete)

  def submit(trackId: TrackId, file: LyFile): Unit = {
    val renderId = renderingService.render(file)
    logger.debug(s"Submitting rendering ($renderId -> $trackId)")

    rendersInProgress += (renderId -> trackId)
  }

  def shutdown(): Unit = renderingService.shutdown()

  override def getRender(submitter: TrackId): Option[File] = {
    rendersCompleted.get(submitter)
  }

  private def complete(taskId: Long, renderingResult: Either[RenderingException, RenderingResult]): Unit = {
    rendersInProgress.get(taskId) match {
      case Some(name) =>
        renderingResult match {
          case Right(result) =>
            logger.debug(s"Rendering [$taskId] successful")
            rendersCompleted += (name -> result.file)
            completionHandler.renderingCompleted(name, success = true)

          case Left(ex) =>
            ex.cause match {
              case Some(cause) => logger.warn(s"Rendering [$taskId] failed", cause)
              case None => logger.warn(s"Rendering [$taskId] failed", ex)
            }
            completionHandler.renderingCompleted(name, success = false)
        }

      case None => logger.warn("Received completed render for unknown submitter!")
    }
    rendersInProgress -= taskId
  }

}
