package music.spelling

import music.collection.Track
import music.primitives.{Key, Position, TimeSignature}
import music.symbols.{Chord, MetaSymbol, Note}

case class TrackSpelling(track: Track) {

  def spelledTimeSignature: Option[TimeSignature] = {
    track
      .getSymbolTrack[MetaSymbol.type]
      .readAt(Position.zero)
      .flatMap(_.props.get[TimeSignature])
      .headOption
  }

  def spelledKey: Option[Key] = {
    track
      .getSymbolTrack[MetaSymbol.type]
      .readAt(Position.zero)
      .flatMap(_.props.get[Key])
      .headOption
  }

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
