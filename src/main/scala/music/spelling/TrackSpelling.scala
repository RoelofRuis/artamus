package music.spelling

import music.collection.Track
import music.primitives.{Key, Position, TimeSignature}
import music.symbols.{Chord, MetaSymbol, Note}

case class TrackSpelling(track: Track) {

  def spelledTimeSignature: Option[TimeSignature] = {
    track
      .getSymbolTrack[MetaSymbol.type]
      .readAt(Position.zero)
      .reverse
      .flatMap(_.get[TimeSignature])
      .headOption
  }

  def spelledKey: Option[Key] = {
    track
      .getSymbolTrack[MetaSymbol.type]
      .readAt(Position.zero)
      .reverse
      .flatMap(_.get[Key])
      .headOption
  }

  def spelledNotes: Seq[Seq[SpelledNote]] =
    track.getSymbolTrack[Note.type].readAllWithPosition
      .map {
        case (_, symbols) => symbols.flatMap(PitchSpelling.spellNote)
      }

  def spelledChords: Seq[SpelledChord] =
    track
      .getSymbolTrack[Chord.type]
      .readAllWithPosition.flatMap { case (_, symbols) =>
      symbols.flatMap(PitchSpelling.spellChord)
    }

}
