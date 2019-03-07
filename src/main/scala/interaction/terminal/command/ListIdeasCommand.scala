package interaction.terminal.command

import application.controller.IdeaController
import javax.inject.Inject

class ListIdeasCommand @Inject() (ideaController: IdeaController) extends Command {

  val name = "list"
  val helpText = "List the available ideas"

  def run(args: Array[String]): CommandResponse = {
    val response = ideaController
      .getAll
      .map(idea => s"${idea.id}. : ${idea.title}")
      .mkString("\n")

    display(response)
  }

}
