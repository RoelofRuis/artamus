package domain.interact

import domain.display.render.Render

object Display {

  final case object Render extends Command

  final case class TrackRendered(render: Render) extends Event

}
