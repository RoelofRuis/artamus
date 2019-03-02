package interaction.terminal.command

class QuitCommand extends Command {

  val name = "quit"
  override val helpText = "Exits the program."

  def run(): CommandResponse = halt

}
