package server

import java.io.File

import protocol.Event


package object rendering {

  final case class RenderingCompleted(key: String, version: Long, success: Boolean) extends Event

  final case class RenderingException(message: String, cause: Option[Throwable]) extends Exception

  final case class RenderingResult(file: File)

}

