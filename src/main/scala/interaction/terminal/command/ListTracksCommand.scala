package interaction.terminal.command

import application.MessageBus
import application.command.IdeaCommand.GetIdea
import application.controller.TrackController
import javax.inject.Inject

import scala.util.Success

class ListTracksCommand @Inject() (
  messageBus: MessageBus,
  trackController: TrackController
) extends Command {

  val name = "tracks"
  val helpText = "List the available tracks, per idea"

  def run(args: Array[String]): CommandResponse = {
    val response = trackController.getAll
      .groupBy(_.ideaId)
      .map { case (ideaId, tracks) => (messageBus.execute(GetIdea(ideaId)), tracks) }
      .collect {
        case (Success(idea), tracks) =>
          val trackData = tracks.map(track => s" - [${track.id}](${track.trackType}) - ${track.ticksPerQuarter}").mkString("\n")
          s"[${idea.id}] ${idea.title} :\n$trackData"
      }.mkString("\n")

    display(response)
  }

}
