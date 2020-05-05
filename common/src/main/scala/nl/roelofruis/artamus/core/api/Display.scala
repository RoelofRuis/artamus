package nl.roelofruis.artamus.core.api

import nl.roelofruis.artamus.core.model.display.render.Render

object Display {

  final case object Render extends Command

  final case class TrackRendered(render: Render) extends Event

}
