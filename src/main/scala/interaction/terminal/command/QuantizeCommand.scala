package interaction.terminal.command

import application.controller.TrackController
import application.model.{ID, Idea}
import interaction.terminal.Prompt
import javax.inject.Inject

import scala.util.Try

class QuantizeCommand @Inject() (
  prompt: Prompt,
  controller: TrackController
) extends Command {

  val name = "quant"
  val helpText = "Quantize an idea"
  override val argsHelp = Some("[id: Int] [subdiv: Int = 1] [errMul: Int = 10]")

  def run(args: Array[String]): CommandResponse = {
    val res: Try[CommandResponse] = for {
      id <- Try(ID[Idea.type](args(0).toLong))
      subdivision <- Try(args(1).toInt) recover { case _ => 1 }
      gridErrorMultiplier <- Try(args(2).toInt) recover { case _ => 10 }
    } yield {
      controller.quantize(id, subdivision, gridErrorMultiplier)
      display(s"Quantized the idea")
    }

    returnRecovered(res)
  }

}
