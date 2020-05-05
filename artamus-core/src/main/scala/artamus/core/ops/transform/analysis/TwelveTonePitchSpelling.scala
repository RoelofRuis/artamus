package artamus.core.ops.transform.analysis

import artamus.core.model.primitives.{Note, _}

object TwelveTonePitchSpelling {

  import artamus.core.ops.transform.analysis.TwelveToneTuning._

  def spellChord(chord: Chord, key: Key): PitchSpelling = spellPc(chord.root, key)

  def spellNote(note: Note, key: Key): ScientificPitch = {
    val spelledPitch = spellPc(note.pitchClass, key)

    val newOctave = spelledPitch.span match {
      case span if span > tuning.span => Octave(note.octave.value - 1)
      case span if span < 0 => Octave(note.octave.value + 1)
      case _ => note.octave
    }

    ScientificPitch(spelledPitch, newOctave)
  }

  private def spellPc(pc: PitchClass, key: Key): PitchSpelling = {
    val keyPcs = key.scale.stepSizes

    tuning
      .possibleIntervals(key.root.toPc, pc)
      .map { interval =>
        val spelling = key.root.addInterval(interval)
        val keyScore = if (keyPcs.contains(spelling.toPc.value)) 0 else 1
        val accidentalScore = spelling.accidental.value.abs
        (keyScore, accidentalScore, spelling)
      }
      .minBy { case (keyScore, accidentalScore, _) => (keyScore, accidentalScore) }._3
  }

}
