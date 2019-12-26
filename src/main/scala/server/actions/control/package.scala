package server.actions

import protocol.v2.Command2

package object control {

  final case class Disconnect(shutdownServer: Boolean) extends Command2
  final case class Authenticate(userName: String) extends Command2

}
