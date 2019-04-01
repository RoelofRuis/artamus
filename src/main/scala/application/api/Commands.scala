package application.api

import application.model.event.MidiTrack.Track_ID
import application.model.symbolic.Track

object Commands {

  trait Command {
    type Res
  }

  // Application
  case object CloseApplication extends Command { type Res = Unit }
  case object GetDevices extends Command { type Res = Array[String] }

  // Track
  case object StartRecording extends Command { type Res = Unit }
  case class StoreRecorded() extends Command { type Res = (Track_ID, Int) }
  case class Play(trackId: Track_ID) extends Command { type Res = Unit }
  case class Quantize(trackId: Track_ID, subdivision: Int, gridErrorMultiplier: Int) extends Command { type Res = Track_ID }
  case object GetAll extends Command { type Res = Iterable[Track_ID] }
  case class ToSymbolTrack(trackId: Track_ID) extends Command { type Res = Track }

}
