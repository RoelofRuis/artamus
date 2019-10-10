package server.domain

import music.primitives._
import protocol.{Command, Query}

package object track {

  // Commands
  case object NewTrack extends Command

  case class SetTimeSignature(t: TimeSignature) extends Command
  case class SetKey(k: Key) extends Command

  case class AddNote(position: Position, duration: Duration, octave: Octave, pitchClass: PitchClass) extends Command

  // Queries
  case object GetMidiPitches extends Query { type Res = List[List[Int]] }

}
