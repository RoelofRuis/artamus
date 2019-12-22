package server

import server.rendering.RenderingConfig
import storage.FileStorageConfig

trait ServerConfig extends RenderingConfig with FileStorageConfig {

  val port = 9999

  val resourceRootPath = "data"
  val cleanupLySources = false
  val pngResolution = 160
  val lyVersion: String = "2.18"
  val paperSize: String = "a6landscape"

  val dbRoot: String = "data/store"
  val cleanupThreshold: Int = 20

}
