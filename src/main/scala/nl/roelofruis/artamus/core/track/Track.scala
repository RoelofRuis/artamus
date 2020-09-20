package nl.roelofruis.artamus.core.track

import nl.roelofruis.artamus.core.track.Layer.MetreTrack

final case class Track(
  metres: MetreTrack,
  layers: Seq[Layer] = Seq()
)
