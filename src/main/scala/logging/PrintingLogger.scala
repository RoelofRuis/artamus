package logging

import core.application.Registerable
import core.components.Logger

class PrintingLogger extends Logger with Registerable {

  def id = "printing-logger"
  def default = false

  def debug(text: String): Unit = println(s"[DEBUG] $text")

}
