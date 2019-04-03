package application.api

import application.model.Track

object Commands {

  trait Command {
    type Res
  }

  // Application
  case object CloseApplication extends Command { type Res = Unit }
  case object GetDevices extends Command { type Res = Array[String] }

  // Track
  case object StartRecording extends Command { type Res = Unit }
  case class StoreRecorded() extends Command { type Res = (Track.TrackID, Int) }
  case class Play(trackId: Track.TrackID) extends Command { type Res = Unit }
  case class Quantize(trackId: Track.TrackID, subdivision: Int, gridErrorMultiplier: Int) extends Command { type Res = Track.TrackID }
  case object GetAll extends Command { type Res = Iterable[Track.TrackID] }
  case class GetTrack(trackId: Track.TrackID) extends Command { type Res = Track }

}
