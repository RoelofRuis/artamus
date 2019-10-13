package music.spelling

import music.collection.{Track, TrackSymbol}
import music.primitives._
import music.symbols.{Chord, Key, Note, TimeSignature}

case class TrackSpelling(track: Track) {

  def spelledTimeSignature: Option[TimeSignatureDivision] = {
    track
      .getSymbolTrack[TimeSignature.type]
      .readAt(Position.zero)
      .reverse
      .flatMap(_.get[TimeSignatureDivision])
      .headOption
  }

  def spelledKey: Option[TrackSymbol[Key.type]] = {
    track
      .getSymbolTrack[Key.type]
      .readAt(Position.zero)
      .reverse
      .headOption
  }

  def spelledNotes: Seq[Seq[SpelledNote]] = {
    track.getSymbolTrack[Note.type].readAllWithPosition
      .map {
        case (_, symbols) => symbols.flatMap(note => PitchSpelling.spellNote(note))
      }
  }

  def spelledChords: Seq[SpelledChord] = {
    track
      .getSymbolTrack[Chord.type]
      .readAllWithPosition.flatMap { case (_, symbols) =>
        symbols.flatMap(chord => PitchSpelling.spellChord(chord))
      }
  }

}
