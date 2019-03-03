package interaction.terminal.command

trait Command extends ResponseFactory {

  val name: String
  val helpText: String = ""

  def run(): CommandResponse

}
