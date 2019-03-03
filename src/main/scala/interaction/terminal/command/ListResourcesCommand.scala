package interaction.terminal.command

import core.application.ResourceManager
import javax.inject.Inject

class ListResourcesCommand @Inject() (
  resourceManager: ResourceManager
) extends Command {

  val name = "list-resources"
  override val helpText = "List the currently open application resources"

  def run(): CommandResponse = {
    val resourceString = resourceManager
      .getRegisteredResources
      .zipWithIndex
      .map { case (resourceName, idx) => s"$idx. $resourceName"}
      .mkString("\n")

    display(resourceString)
  }

}
