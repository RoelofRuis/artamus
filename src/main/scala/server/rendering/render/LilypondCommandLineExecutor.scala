package server.rendering.render

import java.io.{File, PrintWriter}
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.{ExecutorService, Executors}

import javax.annotation.concurrent.NotThreadSafe
import server.rendering.{RenderingException, RenderingResult}
import server.rendering.interpret.LilypondFile

@NotThreadSafe
private[rendering] class LilypondCommandLineExecutor(
  val resourceRootPath: String,
  val cleanupLySources: Boolean,
) {

  private val executor: ExecutorService = Executors.newFixedThreadPool(1)
  private val taskIdGenerator = new AtomicLong(0L)
  private var completionHandler: Option[(Long, Either[RenderingException, RenderingResult]) => Unit] = None

  def render(lilyFile: LilypondFile): Long = {
    val taskId = taskIdGenerator.getAndIncrement()

    executor.execute(makeRunnable(lilyFile, taskId))

    taskId
  }

  def shutdown(): Unit = executor.shutdown()

  def setCompletionHandler(handler: (Long, Either[RenderingException, RenderingResult]) => Unit): Unit = {
    completionHandler = Some(handler)
  }

  private def complete(taskId: Long, result: Either[RenderingException, RenderingResult]): Unit = {
    // TODO: ensure thread safety!
    completionHandler.foreach(_(taskId, result))
  }

  private def makeRunnable(lilyFile: LilypondFile, taskId: Long): Runnable = {
    () => {
      val sourceFile = new File(s"$resourceRootPath/lily_$taskId.ly")
      val targetFile = new File(s"$resourceRootPath/lily_$taskId.png")

      try {
        val writer = new PrintWriter(sourceFile)
        writer.write(lilyFile.getStringContents)
        writer.close()

        import sys.process._

        val result = getLilypondCommand(sourceFile.getAbsolutePath).!!

        if (targetFile.exists()) complete(taskId, Right(RenderingResult(targetFile)))
        else complete(taskId, Left(RenderingException(result, None)))

      } catch {
        case ex: Exception => complete(taskId, Left(RenderingException("Exception during rendering", Some(ex))))
      } finally {
        if (cleanupLySources) sourceFile.delete()
      }
    }
  }

  /**
    * @see http://lilypond.org/doc/v2.18/Documentation/usage/command_002dline-usage
    */
  private def getLilypondCommand(outputPath: String): String = {
    s"lilypond -fpng -odata $outputPath"
  }

}
