package server.domain

import music._
import protocol.{Command, Event, Query}

package object track {

  // Commands
  case class SetTimeSignature(t: TimeSignature) extends Command
  case class SetKey(k: Key) extends Command

  case class AddNote(position: Position, duration: Duration, midiPitch: MidiPitch) extends Command

  // Events
  case object TrackSymbolsUpdated extends Event

  // Queries
  case object GetTrackMidiNotes extends Query { type Res = List[Int] }

}
