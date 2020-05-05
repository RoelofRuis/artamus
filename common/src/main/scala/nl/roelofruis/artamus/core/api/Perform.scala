package nl.roelofruis.artamus.core.api

import nl.roelofruis.artamus.core.model.perform.TrackPerformance

object Perform {

  final case object PreparePerformance extends Query { type Res = TrackPerformance }

}
