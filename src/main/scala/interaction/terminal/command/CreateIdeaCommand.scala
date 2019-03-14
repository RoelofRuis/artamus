package interaction.terminal.command

import application.controller.{IdeaController, TrackController}
import interaction.terminal.Prompt
import javax.inject.Inject

class CreateIdeaCommand @Inject() (
  prompt: Prompt,
  ideaController: IdeaController,
  trackController: TrackController
) extends Command {

  val name = "idea"
  val helpText = "Write down a new idea"

  def run(args: Array[String]): CommandResponse = {
    val title = prompt.read("Idea title")

    val idea = ideaController.create(title)

    trackController.record(idea.id)

    display(s"Created idea [$title] with id [${idea.id}]")
  }

}
