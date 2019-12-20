package server.rendering

import music.model.display.render.Render
import server.rendering.RenderingCompletionHandler.RenderingException

trait RenderingCompletionHandler {
  def renderingCompleted(result: Either[RenderingException, Render]): Unit
}

object RenderingCompletionHandler {

  final case class RenderingException(message: String, cause: Option[Throwable]) extends Exception

}