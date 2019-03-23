package application.api

import application.domain.Idea.Idea_ID
import application.domain.Track.Track_ID

import scala.reflect.runtime.universe._

object Commands {

  abstract class Command[Res: TypeTag]

  // Application
  case object CloseApplication extends Command[Unit]

  // Idea
  case class CreateIdea(title: String) extends Command[Idea_ID]
  case object GetAllWithTracks extends Command[Iterable[(Idea_ID, String, Iterable[Track_ID])]]

  // Track
  case object StartRecording extends Command[Unit]
  case class StoreRecorded(ideaId: Idea_ID) extends Command[(Track_ID, Int)]
  case class Play(trackId: Track_ID) extends Command[Unit]
  case class Quantize(trackId: Track_ID, subdivision: Int, gridErrorMultiplier: Int) extends Command[Track_ID]

}
