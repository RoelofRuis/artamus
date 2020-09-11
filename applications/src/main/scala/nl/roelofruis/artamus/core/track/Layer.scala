package nl.roelofruis.artamus.core.track

import nl.roelofruis.artamus.core.common.Containers.{TemporalInstantMap, TemporalMap}
import nl.roelofruis.artamus.core.track.Pitched.Chord
import nl.roelofruis.artamus.core.track.Temporal.Metre

sealed trait Layer

object Layer {

  final case class ChordLayer(
    metres: TemporalInstantMap[Metre],
    chords: TemporalMap[Chord]
  ) extends Layer

}