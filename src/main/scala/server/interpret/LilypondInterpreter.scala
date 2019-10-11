package server.interpret

import music.collection.Track
import music.primitives._
import music.spelling.TrackSpelling
import music.symbols.MetaSymbol
import server.interpret.lilypond.{ChordNames, LyFile, Staff}

class LilypondInterpreter(
  lyVersion: String,
  paperSize: String
) {

  def interpret(track: Track): LyFile = {
    val spelling = TrackSpelling(track)

    LyFile(
      Staff(
        track.getSymbolTrack[MetaSymbol.type].readAt(Position.zero).headOption.flatMap(_.props.get[Key]),
        track.getSymbolTrack[MetaSymbol.type].readAt(Position.zero).headOption.flatMap(_.props.get[TimeSignature]),
        spelling.spelledNotes,
      ),
      ChordNames(spelling.spelledChords),
      lyVersion,
      paperSize,
    )
  }

}
