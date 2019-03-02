package interaction.terminal.command

trait Command extends ResponseWriter {

  val name: String
  val helpText: String = ""

  /**
    * Run This command
    *
    * @return Whether it was successful
    */
  def run(): CommandResponse

}
