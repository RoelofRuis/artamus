package interaction.terminal

import application.api.Events.PlaybackRequest
import application.api.{CommandBus, EventBus}
import interaction.terminal.command.{Command, Continue, ResponseFactory}
import javax.inject.Inject

import scala.collection.immutable

// TODO: refactor to become (configurable) Driver
class TerminalDriver @Inject() (
  prompt: Prompt,
  unsortedCommands: immutable.Set[Command]
) extends ResponseFactory {

  implicit object CommandOrdering extends Ordering[Command] {
    def compare(command1: Command, command2: Command): Int = command2.name.compareTo(command1.name)
  }

  private val commands = immutable.SortedSet[Command]() ++ unsortedCommands

  def run(bus: CommandBus, eventBus: EventBus): Unit = {
    setSubscriptions(eventBus)
    runInternal(bus)
  }

  private def setSubscriptions(eventBus: EventBus): Unit = {
    eventBus.subscribe[PlaybackRequest](r => TerminalPlayback.playback(prompt, r.track))
  }

  private def runInternal(bus: CommandBus): Unit = {
    val input: Array[String] = prompt.read("Enter command").split(" ")

    val commandName = input.headOption.getOrElse("")

    val response = if (commandName == "help") display(helpText)
    else {
      commands.find(_.name == commandName) match {
        case Some(command) => command.execute(bus, input.tail)
        case None => display(s"unknown command [$commandName]\n$helpText")
      }
    }

    response.response.foreach(prompt.write)
    if (response.action == Continue) runInternal(bus)
  }

  private def helpText: String = commands.map(c => s"${(c.name +: c.argsHelp.toSeq).mkString(" ")}: ${c.helpText}").mkString("\n")

}
