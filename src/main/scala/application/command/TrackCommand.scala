package application.command

import application.model.Idea.Idea_ID
import application.model.Track
import application.model.Track.Track_ID

object TrackCommand {

  case object StartRecording extends Command[Unit]

  case class StoreRecorded(ideaId: Idea_ID) extends Command[Track]

  case class Play(trackId: Track_ID) extends Command[Unit]

  case class Quantize(trackId: Track_ID, subdivision: Int, gridErrorMultiplier: Int) extends Command[Track]

  case object GetAll extends Command[Iterable[Track]]

}
