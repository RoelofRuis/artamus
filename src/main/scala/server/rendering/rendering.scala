package server

import java.io.File

import protocol.Event
import server.domain.track.container.Track

package object rendering {

  trait RenderingConfig {
    val resourceRootPath: String
    val cleanupLySources: Boolean
  }

  trait Renderer {

    def submit(submitter: String, track: Track): Unit

    def shutdown(): Unit

  }

  final case class RenderingCompleted(key: String, version: Long, success: Boolean) extends Event

  final case class RenderingException(message: String, cause: Option[Throwable]) extends Exception

  final case class RenderingResult(file: File)

}
