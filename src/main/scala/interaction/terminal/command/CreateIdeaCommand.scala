package interaction.terminal.command

import application.command.IdeaCommand.CreateIdea
import application.ports.MessageBus

import scala.util.Try

class CreateIdeaCommand extends Command {

  val name = "idea"
  val helpText = "Create a new idea"
  override val argsHelp = Some("[title: String]")

  def execute(bus: MessageBus, args: Array[String]): CommandResponse = {
    val res = for {
      title <- Try(args(0))
      idea <- bus.execute(CreateIdea(title))
    } yield {
      display(s"Created idea [$title] with id [${idea.id}]")
    }

    res getOrElse display("Unable to create idea")
  }

}
