package interaction.terminal.device

import application.ports.Logger
import com.google.inject.Inject
import interaction.terminal.Prompt

class TerminalLogger @Inject() (prompt: Prompt) extends Logger {

  def debug(text: String): Unit = prompt.write(s"[DEBUG] $text")

}
