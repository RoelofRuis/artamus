package server.domain

import music.{Duration, Key, MidiPitch, TimeSignature}
import protocol.{Command, Event, Query}
import util.math.Rational

package object track {

  // Commands
  case class SetTimeSignature(t: TimeSignature) extends Command
  case class SetKey(k: Key) extends Command

  case class AddNote(position: Rational, duration: Duration, midiPitch: MidiPitch) extends Command

  // Events
  case object TrackSymbolsUpdated extends Event

  // Queries
  case object GetTrackMidiNotes extends Query { type Res = List[Int] }

}
