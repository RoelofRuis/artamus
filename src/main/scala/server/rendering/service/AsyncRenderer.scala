package server.rendering.service

import java.io.File

import com.typesafe.scalalogging.LazyLogging
import javax.annotation.concurrent.NotThreadSafe
import javax.inject.Inject
import server.rendering.{Renderer, RenderingCompletionHandler}
import server.interpret.lilypond.LyFile

@NotThreadSafe
private[rendering] class AsyncRenderer @Inject() (
  renderingService: LilypondCommandLineExecutor,
  completionHandler: RenderingCompletionHandler
) extends Renderer with LazyLogging {

  private var rendersInProgress = Map[Long, String]()
  private var rendersCompleted = Map[String, File]()

  renderingService.setCompletionCallback(complete)

  def submit(submitter: String, file: LyFile): Unit = {
    val renderId = renderingService.render(file)
    logger.debug(s"Submitting rendering ($renderId -> $submitter)")

    rendersInProgress += (renderId -> submitter)
  }

  def shutdown(): Unit = renderingService.shutdown()

  override def getRender(submitter: String): Option[File] = {
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
            logger.warn(s"Rendering [$taskId] failed", ex)
            completionHandler.renderingCompleted(name, success = false)
        }

      case None => logger.warn("Received completed render for unknown submitter!")
    }
    rendersInProgress -= taskId
  }

}
