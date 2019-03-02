package interaction.terminal.command

sealed trait ResponseAction
case object Continue extends ResponseAction
case object Halt extends ResponseAction

case class CommandResponse(response: Option[String], action: ResponseAction)

trait ResponseWriter {

  def continue = CommandResponse(None, Continue)
  def halt = CommandResponse(None, Halt)
  def display(text: String) = CommandResponse(Some(text), Continue)

}