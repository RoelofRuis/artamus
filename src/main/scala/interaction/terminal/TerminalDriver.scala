package interaction.terminal

import application.ports.{Driver, MessageBus}
import interaction.terminal.command.{Command, Continue, ResponseFactory}
import javax.inject.Inject

import scala.collection.immutable

class TerminalDriver @Inject() (prompt: Prompt, unsortedCommands: immutable.Set[Command]) extends Driver with ResponseFactory {

  implicit object CommandOrdering extends Ordering[Command] {
    def compare(command1: Command, command2: Command): Int = command2.name.compareTo(command1.name)
  }

  private val commands = immutable.SortedSet[Command]() ++ unsortedCommands

  def run(bus: MessageBus): Unit = {
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
    if (response.action == Continue) run(bus)
  }

  private def helpText: String = commands.map(c => s"${(c.name +: c.argsHelp.toSeq).mkString(" ")}: ${c.helpText}").mkString("\n")

}
