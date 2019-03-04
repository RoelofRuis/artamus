package interaction.terminal.command

import core.application.ServiceRegistry
import core.components.Logger
import interaction.terminal.Prompt
import javax.inject.Inject

class SetLoggerCommand @Inject() (
  loggerRegistry: ServiceRegistry[Logger],
  prompt: Prompt
) extends Command {

  val name = "set-logger"
  override val helpText = "Set the used system logger"

  def run(): CommandResponse = {
    val info = loggerRegistry.getAvailableServices.map { logger =>
      if (logger._1 == loggerRegistry.getActiveName) s" > ${logger._1}"
      else s" - ${logger._1}"
    }.mkString("\n")

    prompt.write(info)

    val selectedLogger = prompt.read("Which logger to use?")

    if (loggerRegistry.makeActive(selectedLogger)) display(s"logger [$selectedLogger] active")
    else display(s"unknown logger [$selectedLogger]")
  }

}
