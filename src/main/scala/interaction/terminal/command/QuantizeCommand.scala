package interaction.terminal.command

import application.command.TrackCommand.Quantize
import application.model.{ID, Track}
import application.ports.MessageBus

import scala.util.Try

class QuantizeCommand extends Command {

  val name = "quant"
  val helpText = "Quantize an idea"
  override val argsHelp = Some("[id: Int] [subdiv: Int = 1] [errMul: Int = 10]")

  def execute(bus: MessageBus, args: Array[String]): CommandResponse = {
    val res: Try[CommandResponse] = for {
      id <- Try(ID[Track](args(0).toLong))
      subdivision <- Try(args(1).toInt) recover { case _ => 1 }
      gridErrorMultiplier <- Try(args(2).toInt) recover { case _ => 10 }
    } yield {
      bus.execute(Quantize(id, subdivision, gridErrorMultiplier))
        .fold(
          ex => display(s"Unable to quantize idea [$ex]"),
          track => display(s"Quantized and stored as track [${track.id}]")
        )
    }

    returnRecovered(res)
  }

}
