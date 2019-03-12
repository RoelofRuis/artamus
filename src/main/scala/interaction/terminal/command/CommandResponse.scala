package interaction.terminal.command

sealed trait ResponseAction
case object Continue extends ResponseAction
case object Halt extends ResponseAction

case class CommandResponse(response: Option[String], action: ResponseAction)

trait ResponseFactory {

  protected def continue = CommandResponse(None, Continue)
  protected def halt = CommandResponse(None, Halt)
  protected def display(text: String) = CommandResponse(Some(text), Continue)

}