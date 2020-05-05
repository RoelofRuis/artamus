package nl.roelofruis.artamus.core.model.write.layers

import nl.roelofruis.artamus.core.model.primitives.{Chord, PitchClass}
import nl.roelofruis.artamus.core.model.write.Chords
import nl.roelofruis.artamus.core.model.write.analysis.{Interpretation, TwelveToneTuning}

object ChordAnalyser {

  def chordLayerForNoteLayer(l: NoteLayer): ChordLayer = {
    val chords = l.defaultVoice
      .readGroups()
      .flatMap { noteGroup =>
        val pitches = noteGroup.notes.map(_.pitchClass)
        val possibleChords = findChords(pitches)

        // TODO: determine best option instead of picking head
        if (possibleChords.nonEmpty) Some(noteGroup.window, possibleChords.head)
        else None
      }
      .foldRight(Chords()) { case ((window, chord), acc) => acc.writeChord(window, chord)}

    ChordLayer(l.metres, l.keys, chords)
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
