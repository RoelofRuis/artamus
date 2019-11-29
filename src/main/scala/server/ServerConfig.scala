package server

import server.rendering.RenderingConfig
import server.storage.StorageConfig

trait ServerConfig extends RenderingConfig with StorageConfig {

  val port = 9999

  val resourceRootPath = "data"
  val cleanupLySources = false
  val pngResolution = 160
  val lyVersion: String = "2.18"
  val paperSize: String = "a6landscape"

  val compactJson: Boolean = false

}
