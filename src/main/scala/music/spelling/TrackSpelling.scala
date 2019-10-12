package music.spelling

import music.collection.Track
import music.primitives._
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

  def spelledNotes: Seq[Seq[SpelledNote]] = {
    val key = spelledKey.getOrElse(Key(SpelledPitch(Step(0), Accidental(0)), Scale.MAJOR))

    track.getSymbolTrack[Note.type].readAllWithPosition
      .map {
        case (_, symbols) => symbols.flatMap(note => PitchSpelling.spellNote(note, key))
      }
  }

  def spelledChords: Seq[SpelledChord] = {
    val key = spelledKey.getOrElse(Key(SpelledPitch(Step(0), Accidental(0)), Scale.MAJOR))

    track
      .getSymbolTrack[Chord.type]
      .readAllWithPosition.flatMap { case (_, symbols) =>
        symbols.flatMap(chord => PitchSpelling.spellChord(chord, key))
      }
  }

}
