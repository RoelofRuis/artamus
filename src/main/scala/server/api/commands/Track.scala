package server.api.commands

import server.util.Rational

object Track {

  case class TrackId(id: Long) extends AnyVal
  case object NewTrack extends Command { type Res = TrackId }
  case class SetTimeSignature(rational: Rational) extends Command { type Res = Unit }
  case class SetKey(k: Int) extends Command { type Res = Unit }

}
