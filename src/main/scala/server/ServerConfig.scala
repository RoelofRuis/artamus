package server

import server.rendering.RenderingConfig

trait ServerConfig extends RenderingConfig {

  val port = 9999

  val resourceRootPath = "data"
  val cleanupLySources = false

}
