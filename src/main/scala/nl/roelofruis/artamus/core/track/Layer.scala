package nl.roelofruis.artamus.core.track

import nl.roelofruis.artamus.core.common.Containers.WindowedSeq
import nl.roelofruis.artamus.core.track.Pitched.{Chord, Key, NoteGroup}
import nl.roelofruis.artamus.core.track.Temporal.Metre
import nl.roelofruis.artamus.core.track.algorithms.rna.Model.RNAAnalysedChord

sealed trait Layer

object Layer {

  type ChordTrack = WindowedSeq[Chord]
  type NoteTrack = WindowedSeq[NoteGroup]
  type RomanNumeralTrack = WindowedSeq[RNAAnalysedChord]
  type KeyTrack = WindowedSeq[Key]
  type MetreTrack = WindowedSeq[Metre]

  final case class ChordLayer(
    chords: ChordTrack
  ) extends Layer

  final case class NoteLayer(
    keys: KeyTrack,
    notes: NoteTrack
  ) extends Layer

}