package server

import java.io.File

package object rendering {

  trait RenderingConfig {
    val resourceRootPath: String
    val cleanupLySources: Boolean
    val pngResolution: Int
  }

  final case class LyFile(contents: String)

  trait Renderer {

    def submit(submitter: String, track: LyFile): Unit

    def getRender(submitter: String): Option[File]

    def shutdown(): Unit

  }

}
