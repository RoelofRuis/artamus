package server.rendering

import java.io.{File, PrintWriter}
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.{ExecutorService, Executors}

import music.write.LilypondFile


class LilypondRenderingService(
  val resourceRootPath: String,
  val cleanupLySources: Boolean,
) {

  private val executor: ExecutorService = Executors.newFixedThreadPool(1)
  private val taskIdGenerator = new AtomicLong(0L)

  def render(lilyFile: LilypondFile): Long = {
    val taskId = taskIdGenerator.getAndIncrement()

    executor.execute(makeRunnable(lilyFile, taskId))

    taskId
  }

  def shutdown(): Unit = executor.shutdown()

  private def complete(id: Long, result: Either[RenderingException, RenderingResult]): Unit = {
    // TODO: ensure thread safety!
    println(s"Rendering [$id]: [$result]")
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

        val result = s"lilypond -fpng -odata ${sourceFile.getAbsolutePath}".!!

        if (targetFile.exists()) complete(taskId, Right(RenderingResult(targetFile)))
        else complete(taskId, Left(RenderingException(result, None)))

      } catch {
        case ex: Exception => complete(taskId, Left(RenderingException("Exception during rendering", Some(ex))))
      } finally {
        if (cleanupLySources) sourceFile.delete()
      }
    }
  }

}
