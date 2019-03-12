package interaction.terminal.command

import application.controller.QuantizationController
import application.model.Idea
import interaction.terminal.Prompt
import javax.inject.Inject

import scala.util.{Success, Try}

class QuantizeCommand @Inject() (
  prompt: Prompt,
  controller: QuantizationController
) extends Command {

  val name = "quant"
  val helpText = "Quantize an idea"
  override val argsHelp = Some("[id: Int]")

  def run(args: Array[String]): CommandResponse = {
    val res: Try[CommandResponse] = for {
      id <- Try(Idea.ID(args(0).toLong))
      _ <- Success(controller.quantize(id))
    } yield display(s"Quantized the idea")

    returnRecovered(res)
  }

}
