package server.api

import server.api.messages.{Command, Event}

object Track {

  case class SetTimeSignature(num: Int, denom: Int) extends Command { type Res = Unit }
  case class SetKey(k: Int) extends Command { type Res = Unit }

  // TODO: don't just restrict to quarter later on!
  case class AddQuarterNote(midiPitch: Int) extends Command { type Res = Unit }

  case object TrackSymbolsUpdated extends Event

}
