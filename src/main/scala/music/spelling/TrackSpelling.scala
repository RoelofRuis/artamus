package music.spelling

import music.collection.Track
import music.primitives._
import music.symbols.{Chord, Key}

@Deprecated // should be added as an algorithm
object TrackSpelling {

  import music.analysis.TwelveToneEqualTemprament._

  implicit class SpellingOps(track: Track) {

    // TODO: read from 'any' position
    def keyAtZero: Option[Key] = {
      track
        .getSymbolTrack[Key]
        .readAt(Position.zero)
        .reverse
        .map(_.symbol)
        .headOption
    }

    def spelledChords: Seq[SpelledChord] = {
      val key = keyAtZero.getOrElse(Key(PitchSpelling(Step(0), Accidental(0)), Scale.MAJOR))

      track
        .getSymbolTrack[Chord]
        .readAll.flatMap { chord => spellChord(chord.symbol, key) }
    }

    private def spellChord(chord: Chord, key: Key): Option[SpelledChord] = {
      for {
        dur <- chord.duration
      } yield SpelledChord(dur, spellPc(chord.root, key))
    }

    private def spellPc(pc: PitchClass, key: Key): PitchSpelling = {
      tuning
        .possibleIntervals(key.root.toPc, pc)
        .map(i => key.root.addInterval(i))
        .toSeq
        .minBy(_.accidental.value.abs)
    }
  }

}
