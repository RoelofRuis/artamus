package server.actions

import protocol.Command

package object control {

  final case class Disconnect(shutdownServer: Boolean) extends Command
  final case class Authenticate(userName: String) extends Command

}
