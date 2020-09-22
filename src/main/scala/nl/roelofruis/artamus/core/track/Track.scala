package nl.roelofruis.artamus.core.track

import nl.roelofruis.artamus.core.track.Layer.{KeyTrack, MetreTrack}

final case class Track(
  metres: MetreTrack,
  keys: KeyTrack,
  layers: Seq[Layer] = Seq()
)
