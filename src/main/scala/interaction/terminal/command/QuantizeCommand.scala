package interaction.terminal.command

import application.api.CommandBus
import application.api.Commands.Quantize
import application.model.event.Track
import application.model.event.domain.ID

import scala.util.Try

class QuantizeCommand extends Command {

  val name = "quant"
  val helpText = "Quantize an idea"
  override val argsHelp = Some("[id: Int] [subdiv: Int = 1] [errMul: Int = 10]")

  def execute(bus: CommandBus, args: Array[String]): CommandResponse = {
    val res: Try[CommandResponse] = for {
      id <- Try(ID[Track](args(0).toLong))
      subdivision <- Try(args(1).toInt) recover { case _ => 1 }
      gridErrorMultiplier <- Try(args(2).toInt) recover { case _ => 10 }
    } yield {
      bus.execute(Quantize(id, subdivision, gridErrorMultiplier))
        .fold(
          ex => display(s"Unable to quantize idea [$ex]"),
          trackId => display(s"Quantized and stored as track [$trackId]")
        )
    }

    returnRecovered(res)
  }

}
