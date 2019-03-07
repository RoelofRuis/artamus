package interaction.terminal.command

import java.util.IllegalFormatConversionException

import application.component.ServiceRegistry
import application.model.Idea
import application.model.repository.GridRepository
import application.ports.PlaybackDevice
import interaction.terminal.Prompt
import javax.inject.Inject

import scala.util.Try

class PlaybackIdeaCommand @Inject() (
  prompt: Prompt,
  gridRepository: GridRepository,
  playbackRegistry: ServiceRegistry[PlaybackDevice]
) extends Command {

  val name = "playback"
  val helpText = "Playback an idea"
  override val argsHelp = Some("<id>")

  def run(args: Array[String]): CommandResponse = {
    Try(Idea.ID(args(0).toLong)).map { id =>
      gridRepository.retrieve(id) match {
        case None => display(s"No Part stored for idea with ID [$id]")
        case Some(data) =>
          playbackRegistry.map(_.play(data))
          continue
      }
    }
      .recover {
        case _: NumberFormatException => display(s"Invalid number [${args(0)}]")
        case _: ArrayIndexOutOfBoundsException => display(s"No Idea ID given")
      }
      .getOrElse(continue)
  }
}
