package server.rendering.impl

import java.io.{File, PrintWriter}
import java.util.UUID

private[rendering] class LilypondCommandLineExecutor(
  val resourceRootPath: String,
  val cleanupLySources: Boolean,
  val pngResolution: Int,
) {

  def render(lilyFile: LyFile): Either[Throwable, RenderingResult] = {
    val fileId = UUID.randomUUID()
    val sourceFile = new File(s"$resourceRootPath/lily_$fileId.ly")
    val targetFile = new File(s"$resourceRootPath/lily_$fileId.png")

    try {
      val writer = new PrintWriter(sourceFile)
      writer.write(lilyFile.contents)
      writer.close()

      import sys.process._

      val result = getLilypondCommand(sourceFile.getAbsolutePath, resourceRootPath).!!

      if (targetFile.exists()) Right(RenderingResult(targetFile))
      else Left(new Exception(s"Target file does not exist: [$result]"))

    } catch {
      case ex: Exception => Left(ex)
    } finally {
      if (cleanupLySources) sourceFile.delete()
    }
  }

  /**
    * @see http://lilypond.org/doc/v2.18/Documentation/usage/command_002dline-usage
    */
  private def getLilypondCommand(sourcePath: String, outputPath: String): String = {
    s"""lilypond -fpng --output="$outputPath" -dresolution=$pngResolution "$sourcePath""""
  }

}
