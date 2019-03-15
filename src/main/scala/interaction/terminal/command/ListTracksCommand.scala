package interaction.terminal.command

import application.controller.{IdeaController, TrackController}
import javax.inject.Inject

class ListTracksCommand @Inject() (
  ideaController: IdeaController,
  trackController: TrackController
) extends Command {

  val name = "tracks"
  val helpText = "List the available tracks, per idea"

  def run(args: Array[String]): CommandResponse = {
    val response = trackController.getAll
      .groupBy(_.ideaId)
      .map { case (ideaId, tracks) => (ideaController.get(ideaId), tracks)}
      .collect {
        case (Some(idea), tracks) =>
          val trackData = tracks.map(track => s" - [${track.id}](${track.trackType}) - ${track.ticksPerQuarter}").mkString("\n")
          s"[${idea.id}] ${idea.title} :\n$trackData"
      }.mkString("\n")

    display(response)
  }

}
