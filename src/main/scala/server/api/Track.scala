package server.api

import music.{Key, TimeSignature}
import protocol.{Command, Event, Query}
import util.math.Rational

object Track {

  // Commands
  case class SetTimeSignature(t: TimeSignature) extends Command
  case class SetKey(k: Key) extends Command

  case class AddNote(position: Rational, duration: Rational, midiPitch: Int) extends Command

  // Events
  case object TrackSymbolsUpdated extends Event

  // Queries
  case object GetTrackMidiNotes extends Query { type Res = List[Int] }

}
