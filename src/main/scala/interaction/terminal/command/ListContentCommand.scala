package interaction.terminal.command

import application.api.Commands.GetAllWithTracks
import application.api.CommandBus

class ListContentCommand extends Command {

  val name = "list"
  val helpText = "List the current ideas with their tracks"

  def execute(bus: CommandBus, args: Array[String]): CommandResponse = {
    val res = for {
      trackData <- bus.execute(GetAllWithTracks)
    } yield {
      val fullText = trackData.map { case (ideaId, ideaName, tracks) =>
        val trackString = tracks.map(t => s" - [$t]").mkString("\n")
        s"[$ideaId] $ideaName : \n$trackString"
      }.mkString("\n")
      display(fullText)
    }

    res.getOrElse(display("Unable to list all tracks"))
  }

}
