package music.spelling

import music.collection.Track
import music.symbols.{Chord, Note}

case class TrackSpelling(track: Track) {

  def spelledNotes: Seq[Seq[SpelledNote]] =
    track.getSymbolTrack[Note.type].readAllWithPosition
      .map { case (_, symbols) =>
        symbols.flatMap { symbol => PitchSpelling.spellNote(symbol.props) }
      }

  def spelledChords: Seq[SpelledChord] =
    track
      .getSymbolTrack[Chord.type]
      .readAllWithPosition.flatMap { case (_, symbols) =>
      symbols.flatMap { symbol => PitchSpelling.spellChord(symbol.props) }
    }

}
