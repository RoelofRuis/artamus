package server.api

import server.model.Track

object Actions {

  trait Action {
    type Res
  }

  // Application
  case object CloseApplication extends Action { type Res = Unit }

  // Track
  case class TrackID(value: Long) extends AnyVal
  case object StartRecording extends Action { type Res = Unit }
  case class StoreRecorded() extends Action { type Res = (TrackID, Int) }
  case class Play(trackId: TrackID) extends Action { type Res = Unit }
  case class Quantize(trackId: TrackID, subdivision: Int, gridErrorMultiplier: Int) extends Action { type Res = TrackID }
  case object GetAll extends Action { type Res = Iterable[TrackID] }
  case class GetTrack(trackId: TrackID) extends Action { type Res = Track }

}
