package domain.interact

object Control {

  final case class Disconnect() extends Command
  final case class Authenticate(userName: String) extends Command

}
