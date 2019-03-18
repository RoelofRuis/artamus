package interaction.terminal.command

import application.MessageBus
import application.command.TrackCommand.Quantize
import application.model.{ID, Track}
import javax.inject.Inject

import scala.util.Try

class QuantizeCommand @Inject() (messageBus: MessageBus) extends Command {

  val name = "quant"
  val helpText = "Quantize an idea"
  override val argsHelp = Some("[id: Int] [subdiv: Int = 1] [errMul: Int = 10]")

  def run(args: Array[String]): CommandResponse = {
    val res: Try[CommandResponse] = for {
      id <- Try(ID[Track](args(0).toLong))
      subdivision <- Try(args(1).toInt) recover { case _ => 1 }
      gridErrorMultiplier <- Try(args(2).toInt) recover { case _ => 10 }
    } yield {
      messageBus.execute(Quantize(id, subdivision, gridErrorMultiplier))
        .fold(
          ex => display(s"Unable to quantize idea [$ex]"),
          track => display(s"Quantized and stored as track [${track.id}]")
        )
    }

    returnRecovered(res)
  }

}
