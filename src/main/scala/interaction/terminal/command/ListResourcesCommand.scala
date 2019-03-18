package interaction.terminal.command

import application.MessageBus
import application.command.ResourceCommand.GetAllResources
import javax.inject.Inject

class ListResourcesCommand @Inject() (messageBus: MessageBus) extends Command {

  val name = "list-resources"
  val helpText = "List the currently registered application resources"

  def run(args: Array[String]): CommandResponse = {
    val resourceString = messageBus.execute(GetAllResources)
      .toOption
      .map(_.mkString("\n"))
      .getOrElse("Failed getting resources")

    display(resourceString)
  }

}
