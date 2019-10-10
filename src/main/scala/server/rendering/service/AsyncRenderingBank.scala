package server.rendering.service

import java.io.File

import com.typesafe.scalalogging.LazyLogging
import javax.annotation.concurrent.NotThreadSafe
import javax.inject.Inject
import server.rendering.interpret.lilypond.LyFile
import server.rendering.{RenderingCompleted, RenderingCompletionHandler, RenderingException, RenderingResult}

@NotThreadSafe
private[rendering] class AsyncRenderingBank @Inject() (
  renderingService: LilypondCommandLineExecutor,
  completionHandler: RenderingCompletionHandler
) extends LazyLogging {

  private var rendersInProgress = Map[Long, String]()
  private var rendersCompleted = Map[String, File]()

  renderingService.setCompletionHandler(complete)

  def submit(submitter: String, file: LyFile): Unit = {
    val renderId = renderingService.render(file)
    logger.debug(s"Submitting rendering ($renderId -> $submitter)")

    rendersInProgress += (renderId -> submitter)
  }

  def shutdown(): Unit = renderingService.shutdown()

  private def complete(taskId: Long, renderingResult: Either[RenderingException, RenderingResult]): Unit = {
    rendersInProgress.get(taskId) match {
      case Some(name) =>
        renderingResult match {
          case Right(result) =>
            logger.debug(s"Rendering successful [$taskId]")
            rendersCompleted += (name -> result.file)
            completionHandler.renderingCompleted(RenderingCompleted(name, taskId, success = true))

          case Left(ex) =>
            logger.warn(s"Rendering failed [$taskId] [$ex]")
            completionHandler.renderingCompleted(RenderingCompleted(name, taskId, success = false))
        }

      case None => logger.warn("Received completed render for unknown submitter!")
    }
    rendersInProgress -= taskId
  }

}
