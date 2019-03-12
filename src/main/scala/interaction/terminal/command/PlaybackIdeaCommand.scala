package interaction.terminal.command

import application.controller.IdeaController
import application.model.{Idea, Quantized, Unquantized}
import interaction.terminal.Prompt
import javax.inject.Inject

import scala.util.Try

class PlaybackIdeaCommand @Inject() (
  prompt: Prompt,
  controller: IdeaController,
) extends Command {

  val name = "playback"
  val helpText = "Playback an idea"
  override val argsHelp = Some("<id> <+|->")

  def run(args: Array[String]): CommandResponse = {
    Try(Idea.ID(args(0).toLong)).flatMap {
      id => {
        Try {
          args(1) match {
            case "+" => Quantized
            case "-" => Unquantized
            case _ => throw new Exception(s"Usage: + = Quantized, - = Unquantized")
          }
        }.map { trackType =>
          if (controller.play(id, trackType)) continue
          else display(s"No Part stored for idea with ID [$id]")
        }
      }
    }
      .recover {
        case _: NumberFormatException => display(s"Invalid number [${args(0)}]")
        case _: ArrayIndexOutOfBoundsException => display(s"Argument missing, requires: $argsHelp")
      }
      .getOrElse(continue)
  }
}
