package server.rendering

import music.domain.write.render.Render

trait RenderingCompletionHandler {
  def renderingCompleted(render: Render): Unit
}
