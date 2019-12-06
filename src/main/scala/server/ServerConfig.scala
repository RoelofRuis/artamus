package server

import server.rendering.RenderingConfig
import server.storage.file.FileStorageConfig

trait ServerConfig extends RenderingConfig with FileStorageConfig {

  val port = 9999

  val resourceRootPath = "data"
  val cleanupLySources = false
  val pngResolution = 160
  val lyVersion: String = "2.18"
  val paperSize: String = "a6landscape"

  val compactJson: Boolean = false
  val dbRoot: Seq[String] = Seq("data", "store")

}
