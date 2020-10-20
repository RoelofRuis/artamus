package nl.roelofruis.artamus.core.track

import nl.roelofruis.artamus.core.common.Temporal.{TemporalVal, Timeline}
import nl.roelofruis.artamus.core.track.Pitched.{Chord, Key, NoteGroup}
import nl.roelofruis.artamus.core.track.Temporal.Metre
import nl.roelofruis.artamus.core.track.algorithms.rna.Model.RNAAnalysedChord

sealed trait Layer

object Layer {

  type ChordTimeline = Timeline[Chord]
  type NoteTimeline = Timeline[NoteGroup]
  type RomanNumeralTimeline = Timeline[RNAAnalysedChord]

  type KeySeq = TemporalVal[Key]
  type MetreSeq = TemporalVal[Metre]

  final case class ChordLayer(chords: ChordTimeline) extends Layer
  final case class NoteLayer(notes: NoteTimeline) extends Layer
  final case class RNALayer(analysis: RomanNumeralTimeline, keys: KeySeq) extends Layer

}