package server.rendering

import java.io.File

package object impl {

  private[impl] final case class LyFile(contents: String)
  private[impl] final case class RenderingResult(file: File)

}
