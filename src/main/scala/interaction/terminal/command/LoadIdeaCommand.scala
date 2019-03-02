package interaction.terminal.command

import core.ID
import core.idea.IdeaRepository
import interaction.terminal.Prompt
import javax.inject.Inject

class LoadIdeaCommand @Inject() (
  prompt: Prompt,
  ideaRepository: IdeaRepository
) extends Command {

  val name = "load"
  override val helpText = "Load an idea"

  def run(): CommandResponse = {
    val id = prompt.read("Input the idea ID")

    ideaRepository.loadMusicData(ID(id.toLong)) match {
      case None => display(s"No data for idea with ID [$id]")
      case Some(value) => display(s"Music data: [$value]")
    }
  }

}
