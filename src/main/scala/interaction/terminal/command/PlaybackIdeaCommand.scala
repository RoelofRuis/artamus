package interaction.terminal.command

import application.controller.IdeaController
import application.model.Idea
import interaction.terminal.Prompt
import javax.inject.Inject

import scala.util.Try

class PlaybackIdeaCommand @Inject() (
  prompt: Prompt,
  controller: IdeaController,
) extends Command {

  val name = "playback"
  val helpText = "Playback an idea"
  override val argsHelp = Some("<id>")

  def run(args: Array[String]): CommandResponse = {
    Try(Idea.ID(args(0).toLong)).map { id =>
      if (controller.play(id)) continue
      else display(s"No Part stored for idea with ID [$id]")
    }
      .recover {
        case _: NumberFormatException => display(s"Invalid number [${args(0)}]")
        case _: ArrayIndexOutOfBoundsException => display(s"No Idea ID given")
      }
      .getOrElse(continue)
  }
}
