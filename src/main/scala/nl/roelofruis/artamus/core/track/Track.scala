package nl.roelofruis.artamus.core.track

import nl.roelofruis.artamus.core.common.Temporal.{TemporalValue, Timeline}
import nl.roelofruis.artamus.core.track.Pitched.{Chord, Key, NoteGroup, RomanNumeral}
import nl.roelofruis.artamus.core.track.Temporal.Metre
import nl.roelofruis.artamus.core.track.Track.{KeyChanges, Layer, MetreChanges}

final case class Track(
  metres: MetreChanges,
  keys: KeyChanges,
  layers: Seq[Layer] = Seq()
)


object Track {

  sealed trait Layer

  type ChordTimeline = Timeline[Chord]
  type NoteTimeline = Timeline[NoteGroup]
  type RomanNumeralTimeline = Timeline[RomanNumeral]

  type KeyChanges = TemporalValue[Key]
  type MetreChanges = TemporalValue[Metre]

  final case class ChordLayer(chords: ChordTimeline) extends Layer
  final case class NoteLayer(notes: NoteTimeline) extends Layer
  final case class RNALayer(analysis: RomanNumeralTimeline, keys: KeyChanges) extends Layer

}