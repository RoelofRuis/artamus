package server.domain

import music.domain.track.{TimeSignature, TrackSymbol}
import music.math.temporal.{Position, Window}
import music.playback.MidiNote
import music.domain.track.symbol.{Chord, Key, Note}
import protocol.{Command, Query}

package object track {

  // Commands
  case object NewTrack extends Command
  case class CreateNoteSymbol(window: Window, symbol: Note) extends Command
  case class CreateKeySymbol(position: Position, symbol: Key) extends Command
  case class CreateTimeSignatureSymbol(position: Position, ts: TimeSignature) extends Command

  // Queries
  case object ReadNotes extends Query { type Res = Seq[TrackSymbol[Note]] }
  case object ReadMidiNotes extends Query { type Res = Seq[MidiNote] }
  case object ReadChords extends Query { type Res = Seq[TrackSymbol[Chord]] }

}
