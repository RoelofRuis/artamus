package server

import protocol.Command

package object control {

  case class Disconnect(shutdownServer: Boolean) extends Command

}
