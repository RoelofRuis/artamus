package music.spelling

import music.collection.{Track, TrackSymbol}
import music.primitives._
import music.symbols.{Chord, Key, Note, TimeSignature}

object TrackSpelling {

   implicit class SpellingOps(track: Track) {

    def spelledTimeSignature: Option[TimeSignatureDivision] = {
      track
        .getSymbolTrack[TimeSignature]
        .readAt(Position.zero) // TODO: read from 'any' position
        .reverse
        .flatMap(_.get[TimeSignatureDivision])
        .headOption
    }

    def spelledKey: Option[TrackSymbol[Key]] = {
      track
        .getSymbolTrack[Key]
        .readAt(Position.zero) // TODO: read from 'any' position
        .reverse
        .headOption
    }

    def spelledNotes: Seq[Seq[SpelledNote]] = {
      track.getSymbolTrack[Note].readAllWithPosition
        .map {
          case (_, symbols) => symbols.flatMap(note => PitchSpelling.spellNote(note))
        }
    }

    def spelledChords: Seq[SpelledChord] = {
      track
        .getSymbolTrack[Chord]
        .readAllWithPosition.flatMap { case (_, symbols) =>
          symbols.flatMap(chord => PitchSpelling.spellChord(chord))
        }
    }
  }

}
