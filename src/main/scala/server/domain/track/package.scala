package server.domain

import music.symbolic.pitch.PitchClass
import music.symbolic.symbol.{Key, Note, TimeSignature}
import music.symbolic.temporal.Position
import protocol.{Command, Query}

package object track {

  // Commands
  case object NewTrack extends Command

  case class SetTimeSignature(t: TimeSignature) extends Command
  case class SetKey(k: Key) extends Command

  case class AddNote(position: Position, note: Note[PitchClass]) extends Command

  // Queries
  case object GetMidiPitches extends Query { type Res = List[List[Int]] }

}
