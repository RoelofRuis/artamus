package interaction.terminal

import scala.io.StdIn

class TerminalPrompt extends Prompt {

  def read(text: String): String = {
    StdIn.readLine(text + " > ")
  }

  def write(text: String): Unit = {
    println(text)
  }

}
