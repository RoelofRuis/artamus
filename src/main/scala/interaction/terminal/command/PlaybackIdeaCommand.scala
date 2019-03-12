package interaction.terminal.command

import application.controller.IdeaController
import application.model.{Idea, Quantized, TrackType, Unquantized}
import interaction.terminal.Prompt
import javax.inject.Inject

import scala.util.Try

class PlaybackIdeaCommand @Inject() (
  prompt: Prompt,
  controller: IdeaController,
) extends Command {

  val name = "playback"
  val helpText = "Playback an idea, either the quantized or unquantized version"
  override val argsHelp = Some("[id: Int] [quant: \"+|-\"]")

  def run(args: Array[String]): CommandResponse = {
    val res = for {
      id <- Try(Idea.ID(args(0).toLong))
      trackType <- Try(parseTrackType(args(1)))
    } yield {
      if (controller.play(id, trackType)) continue
      else display(s"No Track stored for idea [$id $trackType]")
    }

    returnRecovered(res)
  }

  private def parseTrackType: String => TrackType = {
    case "+" => Quantized
    case "-" => Unquantized
    case _ => throw new Exception(s"Usage: + = Quantized, - = Unquantized")
  }
}
