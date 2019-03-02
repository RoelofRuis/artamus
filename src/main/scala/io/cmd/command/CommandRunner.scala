package io.cmd.command

import io.cmd.Prompt
import com.google.inject.Inject
import core.app.AppRunner

import scala.collection.immutable

class CommandRunner @Inject() (prompt: Prompt, commands: immutable.Set[Command]) extends AppRunner {

  def run(): Unit = {
    val input = prompt.read("Enter command")

    val response = if (input == "help") CommandResponse(Some(commands.map(c => s"${c.name}: ${c.helpText}").mkString("\n")), Continue)
    else {
      commands.find(_.name == input) match {
        case Some(command) => command.run()
        case None => CommandResponse(Some(s"uknown command[$input]"), Continue)
      }
    }

    response.response.foreach(prompt.write)
    if (response.action == Continue) run()
  }

}
