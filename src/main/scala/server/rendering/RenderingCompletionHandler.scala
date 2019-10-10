package server.rendering

trait RenderingCompletionHandler {
  def renderingCompleted(result: RenderingCompleted): Unit
}
