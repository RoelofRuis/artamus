package server.rendering

import java.io.File

package object service {

  final case class RenderingException(message: String, cause: Option[Throwable]) extends Exception

  final case class RenderingResult(file: File)

}
