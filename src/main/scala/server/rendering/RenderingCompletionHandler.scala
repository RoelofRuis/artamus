package server.rendering

import music.model.display.render.Render

trait RenderingCompletionHandler {
  def renderingCompleted(result: Either[RenderingException, Render]): Unit
}
