package interaction.terminal.command

import application.component.ResourceManager
import javax.inject.Inject

class ListResourcesCommand @Inject() (
  resourceManager: ResourceManager
) extends Command {

  val name = "list-resources"
  val helpText = "List the currently open application resources"

  def run(args: Array[String]): CommandResponse = {
    val resourceString = resourceManager
      .getRegisteredResources
      .zipWithIndex
      .map { case (resourceName, idx) => s"$idx. $resourceName"}
      .mkString("\n")

    display(resourceString)
  }

}
