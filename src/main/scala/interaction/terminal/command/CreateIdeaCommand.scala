package interaction.terminal.command

import application.controller.IdeaController
import interaction.terminal.Prompt
import javax.inject.Inject

class CreateIdeaCommand @Inject() (
  prompt: Prompt,
  ideaController: IdeaController
) extends Command {

  val name = "idea"
  val helpText = "Create a new idea"

  def run(args: Array[String]): CommandResponse = {
    val title = prompt.read("Idea title")

    val idea = ideaController.create(title)

    display(s"Created idea [$title] with id [${idea.id}]")
  }

}
