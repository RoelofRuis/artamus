package server.rendering

trait RenderingConfig {
  val resourceRootPath: String
  val cleanupLySources: Boolean
  val pngResolution: Int
  val lyVersion: String
  val paperSize: String
}
