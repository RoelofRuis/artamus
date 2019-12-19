package server

import java.io.File

import music.model.write.track.Track.TrackId

package object rendering {

  trait RenderingConfig {
    val resourceRootPath: String
    val cleanupLySources: Boolean
    val pngResolution: Int
  }

  final case class LyFile(contents: String)

  trait Renderer {

    def submit(trackId: TrackId, file: LyFile): Unit

  }

  final case class RenderingException(message: String, cause: Option[Throwable]) extends Exception
  final case class RenderingResult(file: File)

}
