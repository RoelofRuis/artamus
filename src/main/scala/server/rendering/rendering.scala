package server

import java.io.File

import server.rendering.service.LilypondCommandLineExecutor.LyFile

package object rendering {

  trait RenderingConfig {
    val resourceRootPath: String
    val cleanupLySources: Boolean
    val pngResolution: Int
  }

  trait Renderer {

    def submit(submitter: String, track: LyFile): Unit

    def getRender(submitter: String): Option[File]

    def shutdown(): Unit

  }

}
