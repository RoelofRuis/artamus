package nl.roelofruis.artamus.core.track

import nl.roelofruis.artamus.core.common.Containers.{TemporalInstantMap, TemporalMap}
import nl.roelofruis.artamus.core.track.Pitched.{Chord, Key, NoteGroup}

sealed trait Layer

object Layer {

  final case class ChordLayer(
    chords: TemporalMap[Chord]
  ) extends Layer

  final case class NoteLayer(
    keys: TemporalInstantMap[Key],
    notes: TemporalMap[NoteGroup]
  ) extends Layer

}