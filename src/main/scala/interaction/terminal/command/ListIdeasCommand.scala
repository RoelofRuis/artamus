package interaction.terminal.command

import application.model.repository.IdeaRepository
import javax.inject.Inject

class ListIdeasCommand @Inject() (ideaRepository: IdeaRepository) extends Command {

  val name = "list"
  val helpText = "List the available ideas"

  def run(args: Array[String]): CommandResponse = {
    val response = ideaRepository
      .getAll
      .map(idea => s"${idea.id}. : ${idea.title}")
      .mkString("\n")

    display(response)
  }

}
