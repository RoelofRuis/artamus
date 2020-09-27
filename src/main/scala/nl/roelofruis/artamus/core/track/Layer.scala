package nl.roelofruis.artamus.core.track

import nl.roelofruis.artamus.core.common.Temporal.{TemporalVal, WindowedSeq}
import nl.roelofruis.artamus.core.track.Pitched.{Chord, Key, NoteGroup}
import nl.roelofruis.artamus.core.track.Temporal.Metre
import nl.roelofruis.artamus.core.track.algorithms.rna.Model.RNAAnalysedChord

sealed trait Layer

object Layer {

  type ChordSeq = WindowedSeq[Chord]
  type NoteSeq = WindowedSeq[NoteGroup]
  type RomanNumeralSeq = WindowedSeq[RNAAnalysedChord]

  type KeySeq = TemporalVal[Key]
  type MetreSeq = TemporalVal[Metre]

  final case class ChordLayer(chords: ChordSeq) extends Layer
  final case class NoteLayer(notes: NoteSeq) extends Layer
  final case class RNALayer(analysis: RomanNumeralSeq, keys: KeySeq) extends Layer

}