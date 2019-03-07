package interaction.terminal.command

class QuitCommand extends Command {

  val name = "quit"
  val helpText = "Exits the program."

  def run(args: Array[String]): CommandResponse = halt

}
