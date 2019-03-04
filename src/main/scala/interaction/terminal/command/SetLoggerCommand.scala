package interaction.terminal.command

import core.application.ServiceRegistry
import core.components.Logger
import interaction.terminal.Prompt
import javax.inject.Inject

class SetLoggerCommand @Inject() (
  registry: ServiceRegistry[Logger],
  prompt: Prompt
) extends Command {

  val name = "set-logger"
  override val helpText = "Set the used system logger"

  def run(): CommandResponse = {
    val info = registry.getRegistered.map { service =>
      if (registry.getActive.contains(service._1)) s" > ${service._1}"
      else s" - ${service._1}"
    }.mkString("\n")

    prompt.write(info)

    val selectedLogger = prompt.read("Which logger to use?")

    if (registry.setActive(selectedLogger)) display(s"logger [$selectedLogger] active")
    else display(s"unknown logger [$selectedLogger]")
  }

}
