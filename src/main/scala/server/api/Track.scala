package server.api

import protocol.{Command, Event, Query}

object Track {

  case class SetTimeSignature(num: Int, denom: Int) extends Command
  case class SetKey(k: Int) extends Command

  // TODO: don't just restrict to quarter later on!
  case class AddQuarterNote(midiPitch: Int) extends Command

  case object TrackSymbolsUpdated extends Event

  case object GetTrackMidiNotes extends Query { type Res = List[Int] }

}
