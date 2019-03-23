package interaction.terminal.command

import application.api.CommandBus

import scala.util.Try

trait Command extends ResponseFactory {

  val name: String
  val helpText: String
  val argsHelp: Option[String] = None

  def execute(bus: CommandBus, args: Array[String]): CommandResponse

  protected def returnRecovered(response: Try[CommandResponse]): CommandResponse = {
    response.recover {
      case _: Exception => display(s"Invalid arguments, usage:\n$name ${argsHelp.get}")
    }
      .getOrElse(continue)
  }

}
