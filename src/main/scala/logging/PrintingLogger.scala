package logging

import core.components.Logger

class PrintingLogger extends Logger {

  def debug(text: String): Unit = println(s"[DEBUG] $text")

}
