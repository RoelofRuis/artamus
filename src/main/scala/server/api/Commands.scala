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
  case object StartRecording extends Command { type Res = Unit }
  case class StoreRecorded() extends Command { type Res = (TrackID, Int) }
  case class Play(trackId: TrackID) extends Command { type Res = Unit }
  case class Quantize(trackId: TrackID, subdivision: Int, gridErrorMultiplier: Int) extends Command { type Res = TrackID }
  case object GetAll extends Command { type Res = Iterable[TrackID] }
  case class GetTrack(trackId: TrackID) extends Command { type Res = Track }

}
