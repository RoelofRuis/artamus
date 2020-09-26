package nl.roelofruis.artamus.core.track

import nl.roelofruis.artamus.core.track.Layer.{KeySeq, MetreSeq}

final case class Track(
  metres: MetreSeq,
  keys: KeySeq,
  layers: Seq[Layer] = Seq()
)
