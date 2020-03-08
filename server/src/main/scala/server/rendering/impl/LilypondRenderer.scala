package server.rendering.impl

import com.typesafe.scalalogging.LazyLogging
import domain.display.Display
import domain.display.render.Render
import domain.interact.Display.TrackRendered
import domain.interact.Event
import domain.write.Track
import javax.inject.Inject
import server.rendering.Renderer
import storage.api.DbIO

private[rendering] class LilypondRenderer @Inject() (
  renderingService: LilypondCommandLineExecutor,
  interpreter: LilypondInterpreter,
) extends Renderer with LazyLogging {

  import server.model.Renders._

  override def render(track: Track, db: DbIO): List[Event] = {
    logger.debug(s"Rendering track [${track.id}]")

    val displayTrack = Display.displayTrack(track)
    val lyFile = interpreter.interpret(displayTrack)

    renderingService.render(lyFile) match {
      case Right(result) =>
        val render = Render(track.id, result.file.getAbsolutePath)
        logger.debug(s"Render for track [${render.trackId}] successful")
        db.saveRender(render)
        List(TrackRendered(render))
      case Left(ex) =>
        logger.error(s"Render failed", ex)
        List()
    }
  }


}