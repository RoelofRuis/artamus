package domain.write.layers

import domain.primitives.{Chord, PitchClass}
import domain.write.Chords
import domain.write.analysis.{Interpretation, TwelveToneTuning}

object ChordAnalyser {

  def chordLayerForNoteLayer(l: NoteLayer): ChordLayer = {
    val chords = l.notes
      .readGroups()
      .flatMap { noteGroup =>
        val pitches = noteGroup.notes.map(_.pitchClass)
        val possibleChords = findChords(pitches)

        // TODO: determine best option instead of picking head
        if (possibleChords.nonEmpty) Some(noteGroup.window, possibleChords.head)
        else None
      }
      .foldRight(Chords()) { case ((window, chord), acc) => acc.writeChord(window, chord)}

    ChordLayer(l.timeSignatures, l.keys, chords)
  }

  import TwelveToneTuning._ // TODO: set of chords has to be passed in

  private def findChords(set: Seq[PitchClass]): Seq[Chord] = {
    PitchClass.listAll.flatMap{ root =>
      Interpretation.allOf(set.toSet)
        .expand(pc => tuning.possibleIntervals(root, pc))
        .expand(tuning.possibleFunctions)
        .filter(functions => tuning.functionsToName(Set(functions.toSeq: _*)).nonEmpty)
        .data.map(functions => Chord(root, Set(functions.toSeq: _*)))
    }
  }

}
