package api

import protocol.Command

object Control {

  final case class Disconnect() extends Command
  final case class Authenticate(userName: String) extends Command

}
