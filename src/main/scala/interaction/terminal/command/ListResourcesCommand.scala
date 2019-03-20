package interaction.terminal.command

import application.command.ResourceCommand.GetAllResources
import application.ports.MessageBus

class ListResourcesCommand extends Command {

  val name = "list-resources"
  val helpText = "List the currently registered application resources"

  def execute(bus: MessageBus, args: Array[String]): CommandResponse = {
    val resourceString = bus.execute(GetAllResources)
      .toOption
      .map(_.mkString("\n"))
      .getOrElse("Failed getting resources")

    display(resourceString)
  }

}
