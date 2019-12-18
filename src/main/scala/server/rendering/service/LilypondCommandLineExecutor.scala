package server.rendering.service

import java.io.{File, PrintWriter}
import java.util.UUID

import server.rendering.LyFile

private[rendering] class LilypondCommandLineExecutor(
  val resourceRootPath: String,
  val cleanupLySources: Boolean,
  val pngResolution: Int,
) {

  def render(lilyFile: LyFile): Either[RenderingException, RenderingResult] = {
    val fileId = UUID.randomUUID()
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
