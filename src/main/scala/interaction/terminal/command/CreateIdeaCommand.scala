package interaction.terminal.command

import application.api.Commands.CreateIdea
import application.api.CommandBus

import scala.util.Try

class CreateIdeaCommand extends Command {

  val name = "idea"
  val helpText = "Create a new idea"
  override val argsHelp = Some("[title: String]")

  def execute(bus: CommandBus, args: Array[String]): CommandResponse = {
    val res = for {
      title <- Try(args(0))
      id <- bus.execute(CreateIdea(title))
    } yield {
      display(s"Created idea [$title] with id [$id]")
    }

    res getOrElse display("Unable to create idea")
  }

}
