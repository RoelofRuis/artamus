package interaction.terminal.device

import com.google.inject.Inject
import core.components.Logger
import interaction.terminal.Prompt

class TerminalLogger @Inject() (prompt: Prompt) extends Logger {

  def debug(text: String): Unit = prompt.write(s"[DEBUG] $text")

}
