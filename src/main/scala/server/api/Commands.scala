package server.api

import server.model.Track

object Commands {

  trait Command {
    type Res
  }

  // Application
  case object Exit extends Command { type Res = Unit }

  // Track
  case class TrackID(value: Long) extends AnyVal
  case class GetTrack(trackId: TrackID) extends Command { type Res = Track }

}
