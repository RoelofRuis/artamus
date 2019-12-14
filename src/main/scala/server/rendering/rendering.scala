package server

import java.io.File

import music.domain.track.Track.TrackId

package object rendering {

  trait RenderingConfig {
    val resourceRootPath: String
    val cleanupLySources: Boolean
    val pngResolution: Int
  }

  final case class LyFile(contents: String)

  trait Renderer {

    // TODO: fix packaging and references, trackId should not be referenced from here!
    def submit(submitter: TrackId, track: LyFile): Unit

    def getRender(submitter: TrackId): Option[File]

    def shutdown(): Unit

  }

}
