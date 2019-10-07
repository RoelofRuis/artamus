package server.rendering.render

import javax.inject.Inject
import server.domain.track.container.Track
import server.rendering.Renderer
import server.rendering.interpret.LilypondInterpreter

private[rendering] class TrackRenderer @Inject() (
  interpreter: LilypondInterpreter,
  renderingBank: AsyncRenderingBank,
) extends Renderer {

  def submit(submitter: String, track: Track): Unit = {
    val lilyFile = interpreter.interpret(track)
    renderingBank.submit(submitter, lilyFile)
  }

  def shutdown(): Unit = renderingBank.shutdown()

}
