package music.spelling

import music.collection.{Track, TrackSymbol}
import music.primitives._
import music.symbols.{Chord, Key, Note, TimeSignature}

object TrackSpelling {

  import music.analysis.TwelveToneEqualTemprament._

  implicit class SpellingOps(track: Track) {

    // TODO: read from 'any' position
    def timeSignatureAtZero: Option[TimeSignatureDivision] = {
      track
        .getSymbolTrack[TimeSignature]
        .readAt(Position.zero)
        .reverse
        .map(_.symbol.division)
        .headOption
    }

    // TODO: read from 'any' position
    def keyAtZero: Option[TrackSymbol[Key]] = {
      track
        .getSymbolTrack[Key]
        .readAt(Position.zero)
        .reverse
        .headOption
    }

    def spelledNotes: Seq[Seq[SpelledNote]] = {
      val key = keyAtZero.map(_.symbol).getOrElse(Key(SpelledPitch(Step(0), Accidental(0)), Scale.MAJOR))

      track.getSymbolTrack[Note].readAllWithPosition
        .map {
          case (_, symbols) => symbols.map(note => PitchSpelling.spellNote(note.symbol, key))
        }
    }

    def spelledChords: Seq[SpelledChord] = {
      val key = keyAtZero.map(_.symbol).getOrElse(Key(SpelledPitch(Step(0), Accidental(0)), Scale.MAJOR))

      track
        .getSymbolTrack[Chord]
        .readAllWithPosition.flatMap { case (_, symbols) =>
          symbols.flatMap(chord => PitchSpelling.spellChord(chord.symbol, key))
        }
    }
  }

}
