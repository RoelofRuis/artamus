package interaction.terminal.device

import application.ports.Logger
import interaction.terminal.Prompt
import javax.inject.Inject

class TerminalLogger @Inject() (prompt: Prompt) extends Logger {

  def debug(text: String): Unit = prompt.write(s"[DEBUG] $text")

}
