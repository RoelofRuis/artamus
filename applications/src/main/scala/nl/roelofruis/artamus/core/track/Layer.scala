package nl.roelofruis.artamus.core.track

import nl.roelofruis.artamus.core.common.Containers.{TemporalInstantMap, TemporalMap}
import nl.roelofruis.artamus.core.track.Pitched.{Chord, Key, NoteGroup}
import nl.roelofruis.artamus.core.track.Temporal.Metre

sealed trait Layer

object Layer {

  final case class ChordLayer(
    metres: TemporalInstantMap[Metre],
    chords: TemporalMap[Chord]
  ) extends Layer

  final case class NoteLayer(
    metres: TemporalInstantMap[Metre],
    keys: TemporalInstantMap[Key],
    notes: TemporalMap[NoteGroup]
  ) extends Layer

}