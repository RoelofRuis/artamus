package interaction.terminal.command

import application.controller.IdeaController
import interaction.terminal.Prompt
import javax.inject.Inject

class CreateIdeaCommand @Inject() (
  prompt: Prompt,
  controller: IdeaController
) extends Command {

  val name = "idea"
  val helpText = "Write down a new idea"

  def run(args: Array[String]): CommandResponse = {
    val title = prompt.read("Idea title")

    val idea = controller.create(title)

    display(s"Created idea [$title] with id [${idea.id}]")
  }

}
