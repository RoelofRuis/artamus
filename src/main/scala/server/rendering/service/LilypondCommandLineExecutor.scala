package server.rendering.service

import java.io.{File, PrintWriter}
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.{ExecutorService, Executors}

import server.rendering.LyFile

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

private[rendering] class LilypondCommandLineExecutor(
  val resourceRootPath: String,
  val cleanupLySources: Boolean,
  val pngResolution: Int,
) {

  private val executor: ExecutorService = Executors.newFixedThreadPool(1, (r: Runnable) => {
    val t: Thread = Executors.defaultThreadFactory().newThread(r);
    t.setDaemon(true);
    t
  })

  private implicit val executionContext: ExecutionContext = ExecutionContext.fromExecutor(executor)
  private val fileIdGenerator = new AtomicLong(0L)

  def render(lilyFile: LyFile, onComplete: Try[RenderingResult] => Unit): Unit = {
    Future {
      run(lilyFile)
    }.onComplete(res => onComplete(res.flatMap(_.toTry)))
  }

  private def run(lilyFile: LyFile): Either[RenderingException, RenderingResult] = {
    val fileId = fileIdGenerator.getAndIncrement()
    val sourceFile = new File(s"$resourceRootPath/lily_$fileId.ly")
    val targetFile = new File(s"$resourceRootPath/lily_$fileId.png")

    try {
      val writer = new PrintWriter(sourceFile)
      writer.write(lilyFile.contents)
      writer.close()

      import sys.process._

      val result = getLilypondCommand(sourceFile.getAbsolutePath).!!

      if (targetFile.exists()) Right(RenderingResult(targetFile))
      else Left(RenderingException(result, None))

    } catch {
      case ex: Exception => Left(RenderingException("Exception during rendering", Some(ex)))
    } finally {
      if (cleanupLySources) sourceFile.delete()
    }
  }

  /**
    * @see http://lilypond.org/doc/v2.18/Documentation/usage/command_002dline-usage
    */
  private def getLilypondCommand(outputPath: String): String = {
    s"lilypond -fpng -odata -dresolution=$pngResolution $outputPath"
  }

}
