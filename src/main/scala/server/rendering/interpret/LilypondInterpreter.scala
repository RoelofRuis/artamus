package server.rendering.interpret

import music.collection.Track
import music.primitives._
import music.symbols.{Chord, MetaSymbol, Note}
import server.rendering.interpret.lilypond.{ChordNames, LyFile, Staff}

private[rendering] class LilypondInterpreter {

  def interpret(track: Track): LyFile = {
    val stackedNotes: Seq[Seq[SpelledNote]] =
      track.getSymbolTrack[Note.type].readAllWithPosition
        .map { case (_, symbols) =>
          symbols.flatMap { symbol => PitchSpelling.spellNote(symbol.props) }
        }

    val chords = track
      .getSymbolTrack[Chord.type]
      .readAllWithPosition.flatMap { case (_, symbols) =>
        symbols.flatMap { symbol => PitchSpelling.spellChord(symbol.props) }
      }

    LyFile(
      Staff(
        track.getSymbolTrack[MetaSymbol.type].readAt(Position.zero).headOption.flatMap(_.props.get[Key]),
        track.getSymbolTrack[MetaSymbol.type].readAt(Position.zero).headOption.flatMap(_.props.get[TimeSignature]),
        stackedNotes,
      ),
      ChordNames(chords)
    )
  }

}
