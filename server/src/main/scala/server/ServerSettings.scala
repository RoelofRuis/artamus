package server

import server.rendering.RenderingConfig
import storage.FileDatabaseConfig

trait ServerSettings extends RenderingConfig {

  val port = 9999

  val resourceRootPath = "data"
  val cleanupLySources = false
  val pngResolution = 160
  val lyVersion: String = "2.18"
  val paperSize: String = "a6landscape"

  val fileDatabaseConfig: FileDatabaseConfig = FileDatabaseConfig(
    "data/db",
    5,
  )

}
