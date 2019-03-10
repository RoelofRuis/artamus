package interaction.terminal.command

import application.controller.QuantizationController
import application.model.Idea
import interaction.terminal.Prompt
import javax.inject.Inject

import scala.util.Try

class QuantizeCommand @Inject() (
  prompt: Prompt,
  controller: QuantizationController
) extends Command {

  val name = "quant"
  val helpText = "Quantize an idea"
  override val argsHelp = Some("<id>")

  def run(args: Array[String]): CommandResponse = {
    Try(Idea.ID(args(0).toLong)).map { id =>
      controller.quantize(id)
      display(s"Quantized the idea")
    }
      .recover {
        case _: NumberFormatException => display(s"Invalid number [${args(0)}]")
        case _: ArrayIndexOutOfBoundsException => display(s"No Idea ID given")
      }
      .getOrElse(continue)
  }

}
