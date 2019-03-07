package interaction.terminal

import application.ports.Driver
import com.google.inject.Inject
import interaction.terminal.command.{Command, Continue, ResponseFactory}

import scala.collection.immutable

class TerminalDriver @Inject() (prompt: Prompt, commands: immutable.Set[Command]) extends Driver with ResponseFactory {

  def run(): Unit = {
    val input: Array[String] = prompt.read("Enter command").split(" ")

    val commandName = input.headOption.getOrElse("")

    val response = if (commandName == "help") display(helpText)
    else {
      commands.find(_.name == commandName) match {
        case Some(command) => command.run(input.tail)
        case None => display(s"unknown command [$commandName]\n$helpText")
      }
    }

    response.response.foreach(prompt.write)
    if (response.action == Continue) run()
  }

  private def helpText: String = commands.map(c => s"${(c.name +: c.argsHelp.toSeq).mkString(" ")}: ${c.helpText}").mkString("\n")

}
