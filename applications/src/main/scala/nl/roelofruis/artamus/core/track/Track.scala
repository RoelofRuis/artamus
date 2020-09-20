package nl.roelofruis.artamus.core.track

import nl.roelofruis.artamus.core.common.Containers.TemporalInstantMap
import nl.roelofruis.artamus.core.track.Temporal.Metre

final case class Track(
  metres: TemporalInstantMap[Metre],
  layers: Seq[Layer] = Seq()
)
