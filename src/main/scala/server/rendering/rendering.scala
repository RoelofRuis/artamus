package server

import java.io.File


package object rendering {

  final case class RenderingException(message: String, cause: Option[Throwable]) extends Exception

  final case class RenderingResult(file: File)

}

