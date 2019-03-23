package application.api

// TODO: Remove references to domain types, use DTO's for communication between layers
import application.domain.{Idea, Track}
import application.domain.Idea.Idea_ID
import application.domain.Track.Track_ID

import scala.reflect.runtime.universe._

object Commands {

  abstract class Command[Res: TypeTag]

  // Application
  case object CloseApplication extends Command[Unit]

  // Idea
  case class GetIdea(id: Idea_ID) extends Command[Idea]
  case class CreateIdea(title: String) extends Command[Idea]

  // Track
  case object GetAll extends Command[Iterable[Track]]
  case object StartRecording extends Command[Unit]
  case class StoreRecorded(ideaId: Idea_ID) extends Command[Track]
  case class Play(trackId: Track_ID) extends Command[Unit]
  case class Quantize(trackId: Track_ID, subdivision: Int, gridErrorMultiplier: Int) extends Command[Track]

}
