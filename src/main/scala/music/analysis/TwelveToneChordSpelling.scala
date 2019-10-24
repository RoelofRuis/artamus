package music.analysis

import music.symbols.collection.TrackSymbol
import music.primitives._
import music.symbols.{Chord, Key}

object TwelveToneChordSpelling {

  import music.analysis.TwelveToneEqualTemprament._

  def spellChord(chord: TrackSymbol[Chord], keyOption: Option[Key]): TrackSymbol[Chord] = {
    val key = keyOption.getOrElse(Key(PitchSpelling(Step(0), Accidental(0)), Scale.MAJOR))
    chord.update(chord.symbol.withRootSpelling(spellPc(chord.symbol.root, key)))
  }

  private def spellPc(pc: PitchClass, key: Key): PitchSpelling = {
    tuning
      .possibleIntervals(key.root.toPc, pc)
      .map(i => key.root.addInterval(i))
      .toSeq
      .minBy(_.accidental.value.abs) // TODO: this decision might be more complex...
  }

}
