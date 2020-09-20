package nl.roelofruis.artamus.core.track

import nl.roelofruis.artamus.core.common.Containers.{TemporalMap, WindowedSeq}
import nl.roelofruis.artamus.core.track.analysis.rna.Model.RNAAnalysedChord

object Pitched {

  final case class PitchDescriptor(
    step: Int,
    pitchClass: Int
  )

  final case class Degree(
    root: PitchDescriptor,
    quality: Quality,
    relativeTo: Option[PitchDescriptor] = None,
    tritoneSub: Boolean = false
  )

  final case class Quality(
    intervals: Seq[PitchDescriptor]
  )

  final case class Chord(
    root: PitchDescriptor,
    quality: Quality
  )

  final case class Scale(
    pitchClassSequence: Seq[Int]
  )

  final case class Key(
    root: PitchDescriptor,
    scale: Scale
  )

  final case class Note(
    descriptor: PitchDescriptor,
    octave: Octave,
  )

  type Octave = Int
  type NoteGroup = Seq[Note]

  type ChordTrack = TemporalMap[Chord]
  type NoteTrack = TemporalMap[NoteGroup]
  type RomanNumeralTrack = WindowedSeq[RNAAnalysedChord]

}
