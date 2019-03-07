package interaction.terminal.command

import application.controller.ResourceController
import javax.inject.Inject

class ListResourcesCommand @Inject() (
  resourceController: ResourceController
) extends Command {

  val name = "list-resources"
  val helpText = "List the currently registered application resources"

  def run(args: Array[String]): CommandResponse = {
    val resourceString = resourceController.getAll.mkString("\n")

    display(resourceString)
  }

}
