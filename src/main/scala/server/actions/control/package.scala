package server.actions

import protocol.Command

package object control {

  final case class Disconnect() extends Command
  final case class Authenticate(userName: String) extends Command

}
