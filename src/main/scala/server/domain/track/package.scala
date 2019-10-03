package server.domain

import music.symbolic.pitch.Pitch
import music.symbolic.symbol.{Key, TimeSignature}
import music.symbolic.temporal.{Duration, Position}
import protocol.{Command, Query}

package object track {

  // Commands
  case object NewTrack extends Command

  case class SetTimeSignature(t: TimeSignature) extends Command
  case class SetKey(k: Key) extends Command

  case class AddNote(position: Position, duration: Duration, pitch: Pitch) extends Command

  // Queries
  case object GetMidiPitches extends Query { type Res = List[List[Int]] }

}
