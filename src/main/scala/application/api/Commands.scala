package application.api

import application.domain.Idea.Idea_ID
import application.domain.Track.Track_ID

object Commands {

  trait Command {
    type Res
  }

  // Application
  case object CloseApplication extends Command { type Res = Unit }

  // Idea
  case class CreateIdea(title: String) extends Command { type Res = Idea_ID }
  case object GetAllWithTracks extends Command { type Res = Iterable[(Idea_ID, String, Iterable[Track_ID])] }

  // Track
  case object StartRecording extends Command { type Res = Unit }
  case class StoreRecorded(ideaId: Idea_ID) extends Command { type Res = (Track_ID, Int) }
  case class Play(trackId: Track_ID) extends Command { type Res = Unit }
  case class Quantize(trackId: Track_ID, subdivision: Int, gridErrorMultiplier: Int) extends Command { type Res = Track_ID }

}
