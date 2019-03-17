package interaction.terminal.command

import application.controller.IdeaController
import javax.inject.Inject

import scala.util.Try

class CreateIdeaCommand @Inject() (ideaController: IdeaController) extends Command {

  val name = "idea"
  val helpText = "Create a new idea"
  override val argsHelp = Some("[title: String]")

  def run(args: Array[String]): CommandResponse = {
    val res = for {
      title <- Try(args(0))
      idea <- ideaController.create(title)
    } yield {
      display(s"Created idea [$title] with id [${idea.id}]")
    }

    res getOrElse display("Unable to create idea")
  }

}
