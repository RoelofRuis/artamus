package io.cmd

import com.google.inject.Inject
import core.components.AppRunner
import io.cmd.command.{Command, Continue, ResponseWriter}

import scala.collection.immutable

class CommandRunner @Inject() (prompt: Prompt, commands: immutable.Set[Command]) extends AppRunner with ResponseWriter {

  def run(): Unit = {
    val input = prompt.read("Enter command")

    val response = if (input == "help") display(helpText)
    else {
      commands.find(_.name == input) match {
        case Some(command) => command.run()
        case None => display(s"unknown command [$input]\n$helpText")
      }
    }

    response.response.foreach(prompt.write)
    if (response.action == Continue) run()
  }

  private def helpText: String = commands.map(c => s"${c.name}: ${c.helpText}").mkString("\n")

}
