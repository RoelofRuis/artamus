package interaction.terminal.command

import core.idea.IdeaRepository
import javax.inject.Inject

class ListIdeasCommand @Inject() (ideaRepository: IdeaRepository) extends Command {

  val name = "list"
  override val helpText = "List the available ideas"

  def run(): CommandResponse = {
    val response = ideaRepository
      .getAll
      .map(idea => s"${idea.id}. : ${idea.title}")
      .mkString("\n")

    display(response)
  }

}
