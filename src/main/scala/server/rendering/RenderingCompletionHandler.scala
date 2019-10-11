package server.rendering

trait RenderingCompletionHandler {
  def renderingCompleted(submitter: String, success: Boolean): Unit
}
