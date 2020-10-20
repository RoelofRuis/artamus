package nl.roelofruis.artamus.core.track

import nl.roelofruis.artamus.core.track.Layer.{KeyChanges, MetreChanges}

final case class Track(
  metres: MetreChanges,
  keys: KeyChanges,
  layers: Seq[Layer] = Seq()
)
