package server

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

}
