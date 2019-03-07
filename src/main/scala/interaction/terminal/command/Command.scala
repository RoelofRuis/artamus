package interaction.terminal.command

trait Command extends ResponseFactory {

  val name: String
  val helpText: String
  val argsHelp: Option[String] = None

  def run(args: Array[String]): CommandResponse

}
